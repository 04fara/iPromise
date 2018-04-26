from datetime import datetime

from flask import Flask
from flask_jwt_extended import JWTManager
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__, static_folder='files')
app.config.from_pyfile('config.py')

jwt = JWTManager(app)

db = SQLAlchemy(app)

followers = db.Table('followers',
                     db.Column('follower_id', db.Integer, db.ForeignKey('users.user_id')),
                     db.Column('followed_id', db.Integer, db.ForeignKey('users.user_id'))
                     )


class Users(db.Model):
    user_id = db.Column(db.Integer, nullable=False, autoincrement=True, primary_key=True)
    user_name = db.Column(db.String(40), index=True, nullable=False, unique=True)
    password_hash = db.Column(db.String(255), nullable=False)

    followed = db.relationship(
        'Users', secondary=followers,
        primaryjoin=(followers.c.follower_id == user_id),
        secondaryjoin=(followers.c.followed_id == user_id),
        backref=db.backref('followers', lazy='dynamic'), lazy='dynamic')

    posts = db.relationship('Posts', backref='author', lazy='dynamic')

    def followed_posts(self):
        own = Posts.query.filter_by(user_name=self.user_name)
        followed = Posts.query.join(
            followers,
            (Users.query.filter_by(user_id=followers.c.followed_id).first().user_name == Posts.user_name)).filter(
            Users.query.filter_by(user_id=followers.c.follower_id).first().user_name == self.user_name)
        return followed.union(own).order_by(Posts.timestmp.desc())

    @property
    def password(self):
        raise AttributeError('password is not a readable attribute')

    @password.setter
    def password(self, password):
        self.password_hash = generate_password_hash(password)

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)

    def add_post(self, post):
        return self.posts.append(post)

    def remove_post(self, post):
        return self.posts.remove(post)

    def follow(self, user):
        if not self.is_following(user):
            self.followed.append(user)

    def unfollow(self, user):
        if self.is_following(user):
            self.followed.remove(user)

    def is_following(self, user):
        return self.followed.filter(
            followers.c.followed_id == user.user_id).count() > 0

    @property
    def serialize(self):
        return self.__repr__()

    def __repr__(self):
        return {'user_id': self.user_id, 'user_name': self.user_name}


class Posts(db.Model):
    post_id = db.Column(db.Integer, nullable=False, autoincrement=True, primary_key=True)
    user_name = db.Column(db.String(40), db.ForeignKey(Users.user_name), nullable=False, primary_key=True)
    title = db.Column(db.String(40), nullable=False, unique=True)
    description = db.Column(db.String(255), nullable=False)
    timestmp = db.Column(db.DateTime, index=True, nullable=False)
    deadline = db.Column(db.DateTime, nullable=False)

    # received_from = db.Column(db.Integer, db.ForeignKey(Users.user_id))

    @property
    def serialize(self):
        return self.__repr__()

    def __repr__(self):
        return {'post_id': self.post_id, 'user_name': self.user_name, 'title': self.user_name,
                'description': self.description, 'timestmp': self.timestmp.strftime('%Y-%m-%d'),
                'deadline': self.deadline.strftime('%Y-%m-%d')}
