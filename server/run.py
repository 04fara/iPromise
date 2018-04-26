from flask import request, jsonify
from flask_jwt_extended import (
    jwt_required, create_access_token,
    get_jwt_identity,
    create_refresh_token)

from models import Users,Goals,Comments,followers, app, db,jwt

@jwt.expired_token_loader
def expiration(self):
    return jsonify({
        'status': 401,
        'msg': 'The token has expired'
    }), 401

@jwt.invalid_token_loader
def invalid(self):
    return jsonify({
        'status': 403,
        'msg': 'Invalid token'
    }), 403

@jwt.unauthorized_loader
def unauth(self):
    return jsonify({
        'status': 401,
        'msg': 'Unauthorized'
    }), 401

db.create_all()
@app.route('/user', methods=['GET'])
@jwt_required
def index():
    #print(request.headers)
    #print("ASDASD")
    
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    return jsonify(current_user.serialize)
    
    #if not current_user.is_authenticated:
    #    return str(current_user.user_id)
    # else:
    #     login_user(Users.query.get(2))
    #     return str(current_user.user_id)
    # return file.save(os.path.join(app.static_folder), secure_filename(u'i contain cool text.txt'))

@app.route("/findByName",methods=['GET'])
#@jwt_required
def search():
    json = request.get_json()
    users = db.session.query(Users).filter(Users.user_name.op("regexp")(json['regexp']))
    return jsonify([_.serialize for _ in users]),200

@app.route("/getPost",methods=["GET"])
@jwt_required
def getPost():
    json = request.get_json()
    curUser = Users.query.filter_by(user_name=get_jwt_identity()).first()
    myPosts = Goals.query.filter_by(user_id = curUser.user_id,postId = json['postId']).first()
    obj = { 
        'post': post.serialize(),
        'comments': [ _.serialize() for _ in post.comments]
    } 
    return jsonify(obj),200

@app.route('/register', methods=['POST'])
def register():

    json = request.get_json()
    user = Users.query.filter_by(user_name=json['user_name']).first()
    if user:
        return jsonify({'message': 'User "%s" already exists!' % json['user_name']}),401
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
        access_token = create_access_token(identity=user.user_name)
        # refresh_token = create_refresh_token(identity=user.user_name)
        # return jsonify({
        #     'message': 'Logged in as %s' % current_user.user_name,
        #     'access_token': access_token,
        #     'refresh_token': refresh_token
        # })
        return jsonify({'message': 'Logged in as %s.' % user.user_name ,'access_token': access_token})
    else:
        return jsonify({'message': 'Wrong credentials'})

@app.route("/add_comment",methods=['POST'])
@jwt_required
def addComment():
    curUser = Users.query.filter_by(user_name=get_jwt_identity()).first()
    json = request.get_json()
    newComment = Comments(owner=curUser.user_id,postId=json['postId'],postOwner=json['postOwner'],text=json['text'])
    curPost = Goals.query.filter_by(goalId=json['postId'],user_id=json['postOwner']).first()
    curPost.comments.append(newComment)
    db.session.add(curPost)
    db.session.commit()
    return jsonify({"msg":"success"}),200

@app.route("/remove_goal",methods=['POST'])
@jwt_required
def removeGoal():
    json = request.get_json()
    current_user = Users.query.filter_by(user_name=get_jwt_identity()).first()
    post = Goals.query.filter_by(goalId=json['post_id'],user_id=current_user.user_id).first()
    print(get_jwt_identity())
    try:
        current_user.remove_post(post)
        #print([goal.serialize() for goal in current_user.goals])
        #print()
        #db.session.add(current_user)
        db.session.commit()
        return jsonify({'message': 'Post was deleted.'}),200
    except:
        return jsonify({'message': 'Something went wrong'}, 500)


@app.route('/following', methods=['POST'])
# return all users that i'm following
@jwt_required
def users_list():
    cur_user = get_jwt_identity()
    userId = Users.query.filter_by(user_name=cur_user).first()
    #users = map(lambda x: x[1],db.session.query(followers).filter(followers.c.follower_id==userId).all())
    
    return jsonify([_.serialize for _ in userId.followed])


@app.route('/follow', methods=['POST'])
@jwt_required
def follow():
    json = request.get_json()
    print(json)
    username = get_jwt_identity()
    print(username)
    #follower = Users.query.filter_by(user_name=json['follower']).first()
    follower = Users.query.filter_by(user_name=username).first()
    followed = Users.query.filter_by(user_name=json['followed']).first()
    
    try:
        follower.follow(followed)
        db.session.commit()
        return jsonify({'msg': 'success'}),200
    except Exception as ex:
        print(ex)
        return jsonify({'msg': 'Something went wrong'}),400


@app.route('/unfollow', methods=['POST'])
@jwt_required
def unfollow():
    json = request.get_json()
    #follower = Users.query.filter_by(user_name=json['follower']).first()
    follower = Users.query.filter_by(user_name=get_jwt_identity()).first()
    followed = Users.query.filter_by(user_name=json['followed']).first()
    try:
        follower.unfollow(followed)
        db.session.commit()
        return jsonify({'msg': 'successfuly unfollowed {} {}'.format(follower.user_name,followed.user_name)}),200    
    except Exception as ex:
        return jsonify({'message': 'Something went wrong'}),400


@app.route('/is_following', methods=['POST'])
@jwt_required
def is_following():
    json = request.get_json()
    #current_user = Users.query.filter_by(user_name=json['follower']).first()
    curUser = Users.query.filter_by(user_name=get_jwt_identity()).first()

    other_user = Users.query.filter_by(user_name=json['followed']).first()    
    return jsonify({'is_following':curUser.is_following(other_user)})

@app.route('/add_goal',methods=['POST'])
@jwt_required
def addPost():
    json = request.get_json()
    name = get_jwt_identity()
    curUser = Users.query.filter_by(user_name=name).first()
    newPost = Goals(user_id=curUser.user_id,goalTitle=json['title'],userName=name,deadline=json['deadline'],text=json['text'])
    db.session.add(newPost)
    curUser.goals.append(newPost)
    db.session.commit()
    return jsonify([_.serialize() for _ in curUser.goals])

@app.route("/getFeed",methods=['GET'])
@jwt_required
def users():
    curUser = Users.query.filter_by(user_name=get_jwt_identity()).first()
    valueToReturn = []
    for friend in curUser.followed:
        for goal in friend.goals:
            valueToReturn.append(goal.serialize())
    for goal in curUser.goals:
        valueToReturn.append(goal.serialize())
    valueToReturn.sort(key=lambda x : x['Posted on'] ,reverse=True)
    return jsonify(valueToReturn),200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
