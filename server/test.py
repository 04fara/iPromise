import unittest

import requests

from models import db


def clean():
    db.drop_all()


def setup():
    db.create_all()


class UserTest(unittest.TestCase):
    def test_user_test(self):
        response = requests.post('http://localhost:5000/register', json={'user_name': 'testuser', 'password': 'test'})
        self.assertTrue(response.status_code == 200)
        self.assertTrue(response.json() == {'message': 'User testuser was created'})

        response = requests.post('http://localhost:5000/register', json={'user_name': 'testuser', 'password': 'test'})
        self.assertTrue(response.status_code == 401)
        self.assertTrue(response.json() == {'message': 'User testuser already exists'})

        response = requests.post('http://localhost:5000/login', json={'user_name': 'testuser', 'password': 'test'})
        self.assertTrue(response.status_code == 200)
        self.assertTrue(response.json()['message'] == 'Logged in as testuser')

        token = response.json()['access_token']
        headers = {'Authorization': 'Bearer {}'.format(token)}

        response = requests.post('http://localhost:5000/register', json={'user_name': 'alex', 'password': 'alex'})
        self.assertTrue(response.status_code == 200)
        self.assertTrue(response.json() == {'message': 'User alex was created'})

        response = requests.post('http://localhost:5000/follow', json={'other_user': 'alex'}, headers=headers)
        self.assertTrue(response.status_code == 200)
        self.assertTrue(response.json() == {'message': 'Successful'})

        response = requests.post('http://localhost:5000/unfollow', json={'other_user': 'alex'}, headers=headers)
        self.assertTrue(response.status_code == 200)
        self.assertTrue(response.json() == {'message': 'Successful'})

        requests.post('http://localhost:5000/follow', json={'other_user': 'alex'}, headers=headers)

        response = requests.post('http://localhost:5000/login', json={'user_name': 'alex', 'password': 'alex'})
        headers_alex = {'Authorization': 'Bearer {}'.format(response.json()['access_token'])}

        first = requests.post('http://localhost:5000/add_post',
                              json={'title': 'Hm', 'deadline': '2018-2-12', 'description': 'to cake pie'},
                              headers=headers_alex)
        self.assertTrue(first.status_code == 200)
        self.assertTrue(first.json()['message'] == 'Post was created')

        second = requests.post('http://localhost:5000/add_post',
                               json={'title': 'Math', 'deadline': '2018-8-15', 'description': 'to learn math'},
                               headers=headers_alex)
        self.assertTrue(second.status_code == 200)
        self.assertTrue(second.json()['message'] == 'Post was created')

        response = requests.post('http://localhost:5000/get_posts', json={'user_name': 'alex'}, headers=headers)
        self.assertTrue(response.status_code == 200)
        self.assertTrue(len(response.json()) == 2)
        self.assertTrue(response.json() == [first.json()['data'], second.json()['data']])


if __name__ == '__main__':
    UserTest().test_user_test()
