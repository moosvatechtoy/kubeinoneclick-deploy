oneclick = db.getSiblingDB('oneclick');
oneclick.user.drop();
oneclick.user.insert([
    {
        "_id": "admin",
        "team": {"$ref": "team", "$id": "Default"}
    },
    {
        "_id": "user",
        "team": {"$ref": "team", "$id": "Default"}
    }
]);
