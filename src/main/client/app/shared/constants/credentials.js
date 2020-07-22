export const CREDENTIAL_VARIABLES = {
    AWS: [
        {
            name: "access_key",
            type: null,
            description: "",
            defaultValue: "",
            editable: true,
            value: "",
            mandatory: true,
            validationRegex: null
        },
        {
            name: "secret_key",
            type: "password",
            description: "",
            defaultValue: "",
            editable: true,
            value: "",
            mandatory: true,
            validationRegex: null
        },
        {
            name: "region_name",
            type: null,
            description: "",
            defaultValue: null,
            editable: true,
            value: "",
            mandatory: true,
            validationRegex: null
        }
    ],
    AZURE: [
        {
            name: "subscription_id",
            type: null,
            description: "",
            defaultValue: "",
            editable: true,
            value: "",
            mandatory: true,
            validationRegex: null
        },
        {
            name: "client_id",
            type: null,
            description: "",
            defaultValue: "",
            editable: true,
            value: "",
            mandatory: true,
            validationRegex: null
        },
        {
            name: "client_secret",
            type: "password",
            description: "",
            defaultValue: "",
            editable: true,
            value: "",
            mandatory: true,
            validationRegex: null
        },
        {
            name: "tenant_id",
            type: null,
            description: "",
            defaultValue: null,
            editable: true,
            value: "",
            mandatory: true,
            validationRegex: null
        }
    ]
}