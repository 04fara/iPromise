from flask import request, jsonify
from flask_jwt_extended import (
    jwt_required, create_access_token,
    get_jwt_identity,
    create_refresh_token)

from models import Users, app, db


@app.route('/user', methods=['POST'])
def index():
    print(request.headers)
    print("ASDASD")
    current_user = get_jwt_identity()
    return current_user.user_name
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
        # access_token = create_access_token(identity=user.user_name)
        # refresh_token = create_refresh_token(identity=user.user_name)
        # return jsonify({
        #     'message': 'User %s was created' % user.user_name,
        #     'access_token': access_token,
        #     'refresh_token': refresh_token
        # })
        return jsonify({'message': 'User %s was created.' % user.user_name})
    except:
        return jsonify({'message': 'Something went wrong'}, 500)


@app.route('/login', methods=['POST'])
def login():
    json = request.get_json()
    user = Users.query.filter_by(user_name=json['user_name']).first()
    if not user:
        return jsonify({'message': 'User "%s" does not exist!' % json['user_name']})
    if user is not None and user.verify_password(json['password']):
        # access_token = create_access_token(identity=user.user_name)
        # refresh_token = create_refresh_token(identity=user.user_name)
        # return jsonify({
        #     'message': 'Logged in as %s' % current_user.user_name,
        #     'access_token': access_token,
        #     'refresh_token': refresh_token
        # })
        return jsonify({'message': 'Logged in as %s.' % user.user_name})
    else:
        return jsonify({'message': 'Wrong credentials'})


@app.route('/friends', methods=['POST'])
def users_list():
    users = Users.query.order_by(Users.user_name).all()
    return jsonify([_.serialize for _ in users])


@app.route('/follow', methods=['POST'])
def follow():
    json = request.get_json()
    print(json)
    follower = Users.query.filter_by(user_name=json['follower']).first()
    followed = Users.query.filter_by(user_name=json['followed']).first()
    try:
        follower.follow(followed)
        db.session.commit()
        return str(True)
    except Exception as ex:
        return jsonify({'message': 'Something went wrong'})


@app.route('/unfollow', methods=['POST'])
def unfollow():
    json = request.get_json()
    follower = Users.query.filter_by(user_name=json['follower']).first()
    followed = Users.query.filter_by(user_name=json['followed']).first()
    try:
        follower.unfollow(followed)
        db.session.commit()
        return str(True)
    except Exception as ex:
        return jsonify({'message': 'Something went wrong'})


@app.route('/is_following', methods=['POST'])
def is_following():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=json['follower']).first()
    other_user = Users.query.filter_by(user_name=json['followed']).first()
    if current_user == other_user:
        return 'It is you'
    return str(current_user.is_following(other_user))


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
