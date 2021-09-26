# btto-core

## Registration & Login
### Back-end
To register a new user in btto backend use the following request:
```
curl --location --request POST 'https://${BTTO_HOST}/api/v1/users/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "test@test.com",
    "firstName": "First Name",
    "lastName": "Last Name",
    "password": "1234",
    "timezone":"UTC+3"
}'
```

To get user token use this:

```
curl --location --request POST 'https://${CLIENT_ID}:${CLIENT_SECRET}@${BTTO_HOST}/oauth/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=test@test.com' \
--data-urlencode 'password=1234'
```

If you're not a cheater you will get the following answer:

```
{
    "access_token": "long long tocken",
    "token_type": "bearer",
    "expires_in": 43199,
    "scope": "read write",
    "jti": "smaller token"
}
```

Fine, now you can use this *access_token* to work with BTTO! Just like this:

```
curl --location --request GET 'https://${BTTO_HOST}/api/v1/protected' \
--header 'Authorization: Bearer ${The supper long token from the previous answer}'
```
