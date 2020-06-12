let oneclick = db.getSiblingDB('oneclick');
oneclick.team.drop();
oneclick.team.insert([
    {
        "_id": "Default"
    }
]);