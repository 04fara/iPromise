from flask import request, jsonify
from flask_jwt_extended import (
    get_jwt_identity, jwt_required, create_access_token)

from models import Users, app, db, Posts, jwt


@jwt.expired_token_loader
def expiration():
    return jsonify({
        'status': 401,
        'msg': 'The token has expired'
    }), 401


@jwt.invalid_token_loader
def invalid():
    return jsonify({
        'status': 403,
        'msg': 'Invalid token'
    }), 403


@jwt.unauthorized_loader
def unauthorize():
    return jsonify({
        'status': 401,
        'msg': 'Unauthorized'
    }), 401


db.create_all()


@app.route('/user', methods=['GET'])
@jwt_required
def index():
    print(request.headers)
    print("ASDASD")
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    return jsonify(current_user.serialize)
    # if not current_user.is_authenticated:
    #     return str(current_user.user_id)
    # else:
    #     login_user(Users.query.get(2))
    #     return str(current_user.user_id)
    # return file.save(os.path.join(app.static_folder), secure_filename(u'i contain cool text.txt'))


@app.route('/register', methods=['POST'])
def register():
    json = request.get_json()
    user = Users.query.filter_by(user_name=json['user_name']).first()
    if user:
        return jsonify({'message': 'User "%s" already exists!' % json['user_name']})
    user = Users(user_name=json['user_name'], password=json['password'])
    try:
        db.session.add(user)
        db.session.commit()
        return jsonify({'message': 'User %s was created.' % user.user_name})
    except:
        return jsonify({'message': 'Something went wrong'}), 500


@app.route('/login', methods=['POST'])
def login():
    json = request.get_json()
    user = Users.query.filter_by(user_name=json['user_name']).first()
    if not user:
        return jsonify({'message': 'User "%s" does not exist!' % json['user_name']})
    if user is not None and user.verify_password(json['password']):
        access_token = create_access_token(identity=user.user_name)
        return jsonify({'message': 'Logged in as %s.' % user.user_name, 'access_token': access_token})
    else:
        return jsonify({'message': 'Wrong credentials'})


@app.route('/friends', methods=['POST'])
@jwt_required
def users_list():
    current_user = get_jwt_identity()
    users = Users.query.order_by(Users.user_name).all()
    return jsonify([_.serialize for _ in users])


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
        return jsonify({'message': 'Something went wrong'})


@app.route('/unfollow', methods=['POST'])
@jwt_required
def unfollow():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    followed = Users.query.filter_by(user_name=json['followed']).first()
    try:
        current_user.unfollow(followed)
        db.session.commit()
        return str(True)
    except:
        return jsonify({'message': 'Something went wrong'})


@app.route('/is_following', methods=['POST'])
@jwt_required
def is_following():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    other_user = Users.query.filter_by(user_name=json['followed']).first()
    if current_user == other_user:
        return 'It is you'
    return str(current_user.is_following(other_user))


@app.route('/add_post', methods=['POST'])
@jwt_required
def add_post():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    post = Posts(user_name=current_user, title=json['title'], deadline=json['deadline'])
    try:
        current_user.add_post(post)
        return jsonify({'message': 'Post was created.'})
    except:
        return jsonify({'message': 'Something went wrong'}, 500)


@app.route('/remove_post', methods=['POST'])
@jwt_required
def remove_post():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    post = Posts.query.filter_by(post_id=json['post_id']).first()
    try:
        current_user.remove_post(post)
        return jsonify({'message': 'Post was deleted.'})
    except:
        return jsonify({'message': 'Something went wrong'}, 500)


@app.route('/get_posts', methods=['POST'])
@jwt_required
def get_posts():
    json = request.get_json()
    try:
        if json['user_name']:
            return jsonify({
                'posts':
                    [_.serialize for _ in
                     Posts.query.filter_by(user_name=json['user_name']).order_by(Posts.timestamp.desc())]
            })
        else:
            current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
            return jsonify({'posts': [_.serialize for _ in current_user.followed_posts]})
    except:
        return jsonify({'message': 'Something went wrong'})


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
