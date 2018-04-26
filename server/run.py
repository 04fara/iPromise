from datetime import datetime

from flask import request, jsonify
from flask_jwt_extended import (get_jwt_identity, jwt_required, create_access_token)

from models import Users, app, db, Posts, jwt

db.create_all()


@jwt.expired_token_loader
def expiration():
    return jsonify('The token has expired')


@jwt.invalid_token_loader
def invalid():
    return jsonify('Invalid token')


@jwt.unauthorized_loader
def unauthorize():
    return jsonify('Unauthorized')


@app.route('/user', methods=['GET'])
@jwt_required
def index():
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    return jsonify(current_user.serialize)
    # return file.save(os.path.join(app.static_folder), secure_filename(u'i contain cool text.txt'))


@app.route('/register', methods=['POST'])
def register():
    json = request.get_json()
    user = Users.query.filter_by(user_name=json['user_name']).first()
    if user:
        return jsonify('User "%s" already exists' % json['user_name'])
    user = Users(user_name=json['user_name'], password=json['password'])
    try:
        db.session.add(user)
        db.session.commit()
        return jsonify('User %s was created' % user.user_name)
    except:
        return jsonify('Something went wrong'), 500


@app.route('/login', methods=['POST'])
def login():
    json = request.get_json()
    user = Users.query.filter_by(user_name=json['user_name']).first()
    if not user:
        return jsonify('User "%s" does not exist!' % json['user_name'])
    if user is not None and user.verify_password(json['password']):
        access_token = create_access_token(identity=user.user_name)
        return jsonify({'message': 'Logged in as %s' % user.user_name, 'access_token': access_token})
    else:
        return jsonify('Wrong credentials')


@app.route('/followers', methods=['POST'])
@jwt_required
def followers_list():
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    return jsonify([_.serialize for _ in current_user.followers])


@app.route('/followed', methods=['POST'])
@jwt_required
def followed_list():
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    return jsonify([_.serialize for _ in current_user.followed])


@app.route('/follow', methods=['POST'])
@jwt_required
def follow():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    followed = Users.query.filter_by(user_name=json['followed']).first()
    try:
        current_user.follow(followed)
        db.session.commit()
        return str(True)
    except:
        return jsonify('Something went wrong')


@app.route('/unfollow', methods=['POST'])
@jwt_required
def unfollow():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    followed = Users.query.filter_by(user_name=json['followed']).first()
    try:
        current_user.unfollow(followed)
        db.session.commit()
        return jsonify('Successful')
    except:
        return jsonify('Something went wrong')


@app.route('/is_following', methods=['POST'])
@jwt_required
def is_following():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    other_user = Users.query.filter_by(user_name=json['followed']).first()
    if current_user == other_user:
        return jsonify('It is you')
    return jsonify(current_user.is_following(other_user))


@app.route("/search_user", methods=['GET'])
@jwt_required
def search():
    json = request.get_json()
    users = db.session.query(Users).filter(Users.user_name.op("regexp")(json['regexp']))
    return jsonify([_.serialize for _ in users])


@app.route('/add_post', methods=['POST'])
@jwt_required
def add_post():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    now = datetime.now()
    post = Posts(user_name=current_user.user_name, title=json['title'],
                 description=json['description'], timestmp='%s-%s-%s' % (now.year, now.month, now.day),
                 deadline=json['deadline'])
    print(post.serialize)
    try:
        db.session.add(post)
        current_user.add_post(post)
        db.session.commit()
        return jsonify('Post was created')
    except:
        return jsonify('Something went wrong')


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
        return jsonify('Successful')
    except:
        return jsonify('Something went wrong')


@app.route('/get_posts')
@jwt_required
def get_posts():
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    print([_.serialize for _ in current_user.followed_posts()])
    return jsonify([_.serialize for _ in current_user.followed_posts()])


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
