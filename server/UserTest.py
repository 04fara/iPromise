import unittest
import requests
from models import Users,db,followers
class UserTest(unittest.TestCase):
    def User_test(self):
        
        response = requests.post('http://localhost:5000/register',json={"user_name":"damir","password":"damir"})
        #print(response.json())
        self.assertTrue(response.status_code == 200)
        self.assertTrue(response.json() == {'message':'User damir was created.'})
       
        response = requests.post('http://localhost:5000/register',json={'user_name':'damir','password':'damir'})
        #print(response.json())
        self.assertTrue(response.status_code == 401)
        self.assertTrue(response.json() == {'message':'User "damir" already exists!'})
        
        r = requests.post('http://localhost:5000/login',json={'user_name':'damir','password':'damir'})
        #print(r.json())
        self.assertTrue(r.status_code == 200)
        token = r.json()['access_token']
        self.assertTrue(r.json()['message'] == 'Logged in as damir.')
        

        headers = {'Authorization' : 'Bearer {}'.format(token) }
        r = requests.post('http://localhost:5000/add_goal',json={'title':'Some','deadline':'29.05.2019','text':'to kill myself'},headers=headers)
        first = r.json()
        #print(first)
        self.assertTrue(r.status_code == 200)
        self.assertTrue(r.json()[0]['Title']=='Some')
        self.assertTrue(r.json()[0]['owner']=='damir')
        self.assertTrue(r.json()[0]['Goal']=='to kill myself')

        r = requests.post('http://localhost:5000/register',json={'user_name':'alex','password':'alex'})
        r = requests.post('http://localhost:5000/follow',json={'followed':'alex'},headers=headers)
        #print(r.json())
        self.assertTrue(r.status_code == 200)
        #print(r.json())
        self.assertTrue(r.json() == {'msg':'success'})

        r = requests.post('http://localhost:5000/unfollow',json={'followed':'alex'},headers=headers)
        #print(r.json())
        self.assertTrue(r.status_code == 200)
        self.assertTrue(r.json() == {'msg':'successfuly unfollowed damir alex'})

        requests.post('http://localhost:5000/follow',json={'followed':'alex'},headers=headers)
        r = requests.post('http://localhost:5000/login',json={'user_name':'alex','password':'alex'})
        #print(r.json())
        token_alex = r.json()['access_token']
        headers_alex = {'Authorization' : 'Bearer {}'.format(token_alex) }
        second = requests.post('http://localhost:5000/add_goal',json={'title':'Hm','deadline':'28.06.2018','text':'to cake pie'},headers=headers_alex).json()
        third = requests.post('http://localhost:5000/add_goal',json={'title':'Math','deadline':'15.08.2018','text':'to learn math'},headers=headers_alex).json()
        r = requests.get('http://localhost:5000/getFeed',headers=headers)
        
        self.assertTrue(r.status_code == 200)
        self.assertTrue(len(r.json()) == 3)
        self.assertTrue(r.json() == third + first)

    def clean(self):
        #Users.query.delete()
        db.drop_all()
        
    def setup(self):
        db.create_all()

if __name__ == "__main__":
    test = UserTest()
    #test.setup()
    test.User_test()