oneclick = db.getSiblingDB('oneclick');
oneclick.user.drop();
oneclick.user.insert([
    {
        "_id": "admin",
        "team": {"$ref": "team", "$id": "Ze Team"}
    },
    {
        "_id": "Mary J",
        "team": {"$ref": "team", "$id": "Not Ze Team"}
    }
    ,
    {
        "_id": "Darth Vader"
    },
    {
        "_id": "selmak",
        "oAuth2User": {
            "provider": "github",
            "token": "Tok'ra"
        }
    }
]);
