from datetime import datetime

from flask import request, jsonify
from flask_jwt_extended import (get_jwt_identity, jwt_required, create_access_token)

from models import Users, app, db, Posts, jwt

db.create_all()


@jwt.expired_token_loader
def expiration():
    return jsonify({'message': 'The token has expired'})


@jwt.invalid_token_loader
def invalid():
    return jsonify({'message': 'Invalid token'})


@jwt.unauthorized_loader
def unauthorize():
    return jsonify({'message': 'Unauthorized'})


@app.route('/user')
@jwt_required
def index():
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    return jsonify(current_user.serialize), 200
    # return file.save(os.path.join(app.static_folder), secure_filename(u'i contain cool text.txt'))


@app.route('/register', methods=['POST'])
def register():
    json = request.get_json()
    user = Users.query.filter_by(user_name=json['user_name']).first()
    if user is not None:
        return jsonify({'message': 'User %s already exists' % json['user_name']}), 401
    user = Users(user_name=json['user_name'], password=json['password'])
    try:
        db.session.add(user)
        db.session.commit()
        return jsonify({'message': 'User %s was created' % user.user_name}), 200
    except:
        return jsonify({'message': 'Something went wrong'}), 500


@app.route('/login', methods=['POST'])
def login():
    json = request.get_json()
    user = Users.query.filter_by(user_name=json['user_name']).first()
    if user is None:
        return jsonify({'message': 'User "%s" does not exist!' % json['user_name']}), 401
    if user is not None and user.verify_password(json['password']):
        access_token = create_access_token(identity=user.user_name)
        return jsonify({'message': 'Logged in as %s' % user.user_name, 'access_token': access_token}), 200
    else:
        return jsonify({'message': 'Wrong credentials'}), 500


@app.route('/followers', methods=['POST'])
@jwt_required
def followers_list():
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    return jsonify([_.serialize for _ in current_user.followers]), 200


@app.route('/followed', methods=['POST'])
@jwt_required
def followed_list():
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    return jsonify([_.serialize for _ in current_user.followed]), 200


@app.route('/follow', methods=['POST'])
@jwt_required
def follow():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    followed = Users.query.filter_by(user_name=json['other_user']).first()
    try:
        current_user.follow(followed)
        db.session.commit()
        return jsonify({'message': 'Successful'}), 200
    except:
        return jsonify({'message': 'Something went wrong'}), 500


@app.route('/unfollow', methods=['POST'])
@jwt_required
def unfollow():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    followed = Users.query.filter_by(user_name=json['other_user']).first()
    try:
        current_user.unfollow(followed)
        db.session.commit()
        return jsonify({'message': 'Successful'}), 200
    except:
        return jsonify({'message': 'Something went wrong'}), 500


@app.route('/is_following', methods=['POST'])
@jwt_required
def is_following():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    other_user = Users.query.filter_by(user_name=json['other_user']).first()
    if current_user == other_user:
        return jsonify({'message': 'It is you'}), 200
    return jsonify({'message': current_user.is_following(other_user)}), 200


@app.route("/search_user", methods=['POST'])
@jwt_required
def search():
    json = request.get_json()
    users = db.session.query(Users).filter(Users.user_name.op("regexp")(json['regexp']))
    return jsonify([_.serialize for _ in users]), 200


@app.route('/add_post', methods=['POST'])
@jwt_required
def add_post():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    now = datetime.now()
    post = Posts(user_id=current_user.user_id, title=json['title'],
                 description=json['description'], timestmp='%s-%s-%s' % (now.year, now.month, now.day),
                 deadline=json['deadline'])
    try:
        db.session.add(post)
        current_user.add_post(post)
        db.session.commit()
        return jsonify({'data': post.serialize, 'message': 'Post was created'}), 200
    except:
        return jsonify({'message': 'Something went wrong'}), 500


@app.route('/remove_post', methods=['POST'])
@jwt_required
def remove_post():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    post = Posts.query.filter_by(post_id=json['post_id']).first()
    try:
        db.session.remove(post)
        current_user.remove_post(post)
        db.session.commit()
        return jsonify({'message': 'Successful'}), 200
    except:
        return jsonify({'message': 'Something went wrong'}), 500


@app.route('/get_posts', methods=['POST'])
@jwt_required
def get_posts():
    json = request.get_json()
    if 'user_name' in json:
        current_user = Users.query.filter_by(user_name=json['user_name']).first()
        feed = False
    else:
        current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
        feed = True
    return jsonify(current_user.get_posts(feed)), 200


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
