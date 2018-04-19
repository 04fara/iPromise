from datetime import datetime

from flask import jsonify, Flask
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

    goals = db.relationship('Goals', backref='author', lazy='dynamic')

    followed = db.relationship(
        'Users', secondary=followers,
        primaryjoin=(followers.c.follower_id == user_id),
        secondaryjoin=(followers.c.followed_id == user_id),
        backref=db.backref('followers', lazy='dynamic'), lazy='dynamic')

    def get_id(self):
        return self.user_name

    @property
    def is_authenticated(self):
        return True

    @property
    def is_active(self):
        return True

    @property
    def is_anonymous(self):
        return False

    @property
    def password(self):
        raise AttributeError('password is not a readable attribute.')

    @password.setter
    def password(self, password):
        self.password_hash = generate_password_hash(password)

    def __init__(self, user_name, password):
        self.user_name = user_name
        self.password = password

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)

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
        return {
            'user_id': self.user_id,
            'user_name': self.user_name,
        }

    def __repr__(self):
        return jsonify({'user_id': self.user_id, 'user_name': self.user_name})


class Goals(db.Model):
    goalId = db.Column(db.Integer, nullable=False, autoincrement=True, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey(Users.user_id), nullable=False, primary_key=True)
    goalTitle = db.Column(db.String(40), nullable=False, unique=True)
    timestamp = db.Column(db.DateTime, index=True, nullable=False, default=datetime.utcnow)
    deadline = db.Column(db.DateTime, nullable=False)

    def __repr__(self):
        return jsonify({'Title': self.user_name, 'Posted on': self.timestamp, 'Deadline': self.deadline})
