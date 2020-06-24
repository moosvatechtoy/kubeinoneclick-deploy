export const VARIABLES = {
    AWS : [
        { name: "region", editable: false },
        { name: "access_key", defaultValue: "", editable: false },
        { name: "secret_key", defaultValue: "", editable: false }
    ],
    AZURE : [
        { name: "subscription_id", editable: false },
        { name: "client_id", defaultValue: "", editable: false },
        { name: "client_secret", defaultValue: "", editable: false },
        { name: "tenant_id", defaultValue: "", editable: false }
    ],
    GOOGLE : [
        { name: "project", editable: false },
        { name: "region", defaultValue: "", editable: false }
    ],
    ONPREM : [
        { name: "username", editable: false },
        { name: "password", defaultValue: "", editable: false }
    ]
}