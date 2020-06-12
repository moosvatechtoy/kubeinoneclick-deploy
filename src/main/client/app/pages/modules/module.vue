<template>
  <div v-if="module" class="block">
    <div class="block_head">
      <h2>Configuration {{ module.name }}</h2>
    </div>

    <div class="block_content">
      <form>
        <b-form-row>
          <b-col cols="3">
            <b-form-group label="Name" description="The name of your Configuration">
              <b-input id="module.name" v-model="module.name" :state="notEmpty(module.name)" />
              <b-form-invalid-feedback>This field is mandatory</b-form-invalid-feedback>
            </b-form-group>
          </b-col>
          <b-col cols="2">
            <b-form-group label="Provider">
              <vue-multiselect
                v-model="module.mainProvider"
                searchable
                placeholder="Select Provider"
                :show-labels="false"
                required
                :options="['AWS','AZURE', 'GOOGLE', 'ONPREM']"
                @select="onProviderChange"
              />
              <b-form-invalid-feedback>This field is mandatory</b-form-invalid-feedback>
            </b-form-group>
          </b-col>
          <b-col cols="2">
            <b-form-checkbox
              style="margin-top: 30px;"
              v-model="module.remoteRun"
              name="remoteRun-button"
              switch
            >Container</b-form-checkbox>
          </b-col>
          <b-col cols="4" v-if="!module.remoteRun">
            <b-form-group label="Terraform Path" description="Terraform installed location">
              <b-input
                id="module.terraformPath"
                v-model="module.terraformPath"
                :state="notEmpty(module.terraformPath)"
              />
              <b-form-invalid-feedback>Terraform Location is mandatory</b-form-invalid-feedback>
            </b-form-group>
          </b-col>
          <b-col :md="isTerraformImageOverride ? '5' : '3'">
            <app-terraform-image-input
              v-if="module.remoteRun"
              :image="module.terraformImage"
              @form-status="isTerraformImageValid = $event"
              @override-status="isTerraformImageOverride = $event"
            />
          </b-col>
        </b-form-row>

        <b-form-group label="Description" description="The description of your Configuration">
          <b-form-textarea v-model="module.description" />
        </b-form-group>

        <b-form-row>
          <b-col cols="2">
            <b-form-checkbox
              style="margin-top: 10px; margin-bottom: 10px;"
              v-model="module.remoteCode"
              name="remoteRun-button"
              switch
            >Git Repository</b-form-checkbox>
          </b-col>
        </b-form-row>
        <b-form-row>
          <b-col v-if="module.remoteCode">
            <b-form-group
              label="Git Repository URL"
              description="The URL of the Configuration's git repository"
            >
              <b-input
                v-model="module.gitRepositoryUrl"
                :state="notEmpty(module.gitRepositoryUrl)"
              />
              <b-form-invalid-feedback>This field is mandatory</b-form-invalid-feedback>
            </b-form-group>
          </b-col>
          <b-col v-if="module.remoteCode">
            <b-form-group
              label="Git repository directory"
              description="The sub-directory of the Configuration's code inside the repository (leave empty if root)"
            >
              <b-input v-model="module.directory" />
            </b-form-group>
          </b-col>
          <b-col v-if="!module.remoteCode">
            <b-form-group
              label="Local repository directory"
              description="The local directory of the Configuration's code"
            >
              <b-input v-model="module.localCodeLocation" />
            </b-form-group>
          </b-col>
        </b-form-row>

        <h2>Authorized Teams</h2>

        <b-form-row>
          <b-col cols="6">
            <b-form-group>
              <vue-multiselect
                v-model="module.authorizedTeams"
                :multiple="true"
                label="id"
                track-by="id"
                searchable
                placeholder="Select teams"
                :options="teams"
              />
            </b-form-group>
          </b-col>
        </b-form-row>

        <h2>
          Variables
          <b-button variant="success" @click="addVar()">
            <font-awesome-icon icon="plus" />
          </b-button>
        </h2>

        <app-module-variable
          v-for="(modVar, idx) in module.variables"
          :key="idx"
          :variable="modVar"
          @removeVar="removeVar"
        />

        <b-form-group
          v-if="module.mainProvider === 'GOOGLE'"
          label="Google Service Credentials"
          description="Google service account key in json format"
        >
          <b-form-textarea v-model="module.secretKey" @change="encodeCredentials()" />
        </b-form-group>

        <b-button variant="primary" :disabled="!formValid" @click="save">
          <font-awesome-icon icon="save" />Save
        </b-button>
      </form>
    </div>
  </div>
</template>

<script>
import AppModuleVariable from "@/pages/modules/module-variable.vue";
import AppTerraformImageInput from "@/pages/modules/terraform-image-input.vue";
import {
  getModule,
  updateModule,
  createModule
} from "@/shared/api/modules-api";
import { getTeams } from "@/shared/api/teams-api";
import { displayNotification } from "@/shared/services/modal-service";
import { VARIABLES } from "@/shared/constants/variable";

export default {
  name: "AppModule",

  components: {
    AppModuleVariable,
    AppTerraformImageInput
  },

  props: {
    moduleId: {
      type: String,
      required: true
    }
  },

  data: function data() {
    return {
      module: null,
      isTerraformImageValid: null,
      isTerraformImageOverride: null,
      teams: []
    };
  },

  computed: {
    formValid() {
      return (
        [
          this.module.name,
          this.module.mainProvider,
          this.module.remoteCode
            ? this.module.gitRepositoryUrl
            : this.module.localCodeLocation
        ].every(this.notEmpty) &&
        this.module.variables
          .map(variable => variable.name)
          .every(this.notEmpty) &&
        (module.remoteRun ? this.isTerraformImageValid : true)
      );
    }
  },

  async created() {
    console.log(this.moduleId);
    if (this.moduleId === "ADD") {
      let dataModule = {};
      dataModule.moduleMetadata = {};
      dataModule.remoteRun = false;
      dataModule.remoteCode = true;
      dataModule.terraformPath = "/usr/local/terraform";
      dataModule.mainProvider = "AWS";
      dataModule.terraformImage = {
        repository: "hashicorp/terraform",
        tag: "latest"
      };
      dataModule.variables = Object.assign([], VARIABLES['AWS']);
      dataModule.moduleMetadata = {};
      this.module = await dataModule;
      this.isCreate = true;
    } else {
      this.module = await getModule(this.moduleId);
      if (this.module.mainProvider === 'GOOGLE') {
        this.module.variables = this.module.variables.filter(item => item.name !== 'credentials');
      }
      this.isCreate = false;
    }
    this.teams = await getTeams();
  },

  methods: {
    notEmpty(field) {
      return (
        typeof field !== "undefined" && field !== null && field.trim() !== ""
      );
    },
    onProviderChange(provider) {
      if (provider) {
        this.module.variables = Object.assign([], VARIABLES[provider]);
      }
    },
    async save() {
      this.module.secretKey = this.notEmpty(this.module.secretKey)
        ? this.module.secretKey
        : " ";
      if (this.isCreate) {
        await createModule(this.module)
          .then(() => {
            displayNotification(this, {
              message: "Configuration created",
              variant: "success"
            });
            this.$router.push({ name: "modules", params: {} });
          })
          .catch(({ error, message }) =>
            displayNotification(this, {
              title: error,
              message,
              variant: "danger"
            })
          );
      } else {
        await updateModule(this.module)
          .then(() => {
            displayNotification(this, {
              message: "Configuration saved",
              variant: "success"
            });
            this.$router.push({ name: "modules", params: {} });
          })
          .catch(({ error, message }) =>
            displayNotification(this, {
              title: error,
              message,
              variant: "danger"
            })
          );
      }
    },
    removeVar(variable) {
      this.module.variables.splice(
        this.module.variables.findIndex(v => v.name === variable.name),
        1
      );
    },
    addVar() {
      this.module.variables.push({});
    },
    encodeCredentials() {
      let base64 = /^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$/;
      if (!base64.test(this.module.secretKey)) {
        this.module.secretKey = btoa(this.module.secretKey);
      }
    }
  }
};
</script>
