let oneclick = db.getSiblingDB('oneclick');
oneclick.team.drop();
oneclick.team.insert([
    {
        "_id": "Ze Team"
    },
    {
        "_id": "Not Ze Team"
    },
    {
        "_id": "Sith"
    }
]);