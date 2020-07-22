<template>
  <div v-if="module" class="block">
    <form-wizard
      title="Run a new Cluster"
      subtitle="Follow the steps to start a new Cluster"
      color="#00ab94"
      error-color="#dc3545"
    >
      <tab-content title="Cluster" :before-change="checkStackNameValidation">
        <h4>{{ module.name }}</h4>
        <p>{{ module.description }}</p>

        <hr />

        <div class="form-group">
          <b-form-group label="Name" description="The name of your Cluster">
            <b-input v-model="stack.name" :state="stackNameValid" autofocus />
            <b-form-invalid-feedback>This field is mandatory</b-form-invalid-feedback>
          </b-form-group>
          <b-form-group label="Description" description="The description of your Cluster">
            <b-input v-model="stack.description" />
          </b-form-group>
        </div>
      </tab-content>
      <tab-content title="Variables" :before-change="checkStackVariablesValidation">
        <h2>Input variables</h2>
        <app-stack-variable
          v-for="variable in stack.variables"
          :key="variable.name"
          v-model="variable.value"
          v-bind="variable"
          @valid="(isValid) => variable.isValid = isValid"
        />
      </tab-content>
      <tab-content title="Destroy">
        <b-form-group label="Cluster will be destroyed in">
          <b-form-row>
            <b-col cols="2">
              <b-form-radio-group
                id="radio-group-1"
                v-model="stack.destroyType"
                name="destroy-options"
                @change="onDestroyChange"
              >
                <b-form-radio class="destroy-after-option" value="T">Destroy after</b-form-radio>
                <b-form-radio value="D">Specific time limit</b-form-radio>
              </b-form-radio-group>
            </b-col>
            <b-col cols="2" v-if="stack.destroyType == 'T'">
              <b-form-select v-model="stack.destroyAfterHours" :options="destroyAfterTimeOption"></b-form-select>
            </b-col>
            <b-col cols="3" class="specific-time-limit">
              <b-form-datepicker
                v-if="stack.destroyType == 'D'"
                id="destroyAfterTime-datepicker"
                placeholder="Date"
                v-model="stack.destroyAfterDate"
              ></b-form-datepicker>
            </b-col>
            <b-col cols="3" class="specific-time-limit" v-if="stack.destroyType == 'D'">
              <b-form-timepicker v-model="stack.destroyAfterTime" placeholder="Time" locale="en"></b-form-timepicker>
            </b-col>
          </b-form-row>
        </b-form-group>
      </tab-content>
      <tab-content title="Start">
        <h2>Run</h2>
        <p>Save your Cluster or run it !</p>
      </tab-content>

      <!-- customizing save button -->
      <template slot="finish" slot-scope="props">
        <wizard-button
          v-if="props.isLastStep"
          :style="props.fillButtonStyle"
          class="wizard-footer-right finish-button"
          @click.native="save"
        >
          <font-awesome-icon icon="save" />Save
        </wizard-button>
      </template>

      <!-- add run button -->
      <template slot="custom-buttons-right" slot-scope="props">
        <wizard-button
          v-if="props.isLastStep"
          :style="props.fillButtonStyle"
          class="wizard-footer-right finish-button ml-3"
          @click.native="saveAndRun"
        >
          <font-awesome-icon icon="rocket" />Save and run
        </wizard-button>
      </template>
    </form-wizard>

    <b-modal
      id="update-credential-modal"
      ref="modal"
      title="Credentials"
      okVariant="success"
      button-size="sm"
      :ok-disabled="Boolean(!credentialsFormValid)"
      @ok="handleCredentialsOk"
    >
      <form ref="form" @submit.stop.prevent="handleCredentialsSubmit">
        <div
          v-if="module.mainProvider != 'GOOGLE' && credentialVariables"
          class="block-content-modal"
        >
          <app-stack-variable
            v-for="variable in credentialVariables"
            :key="variable.name"
            :name="variable.name"
            :description="variable.description"
            :value="variable.value"
            :validationRegex="variable.validationRegex"
            :mandatory="variable.mandatory"
            v-model="variable.value"
            @valid="(isValid) => variable.isValid = isValid"
          />
        </div>
        <b-form-file
          v-model="googleCredentials"
          :state="Boolean(googleCredentials)"
          placeholder="Google Service Credentials"
          v-if="module.mainProvider == 'GOOGLE'"
          accept=".json"
        ></b-form-file>
        <b-form-checkbox
          id="removeCredentials"
          v-model="stack.removeCredentials"
          name="removeCredentials"
        >Remove Credentials after deploy</b-form-checkbox>
        <b-popover
          target="removeCredentials"
          variant="danger"
          triggers="hover focus"
          placement="bottomright"
        >
          <template v-slot:title>Warning</template>
          You can't perform TTL and Scheduling operations if credentials not saved!
        </b-popover>
      </form>
    </b-modal>
  </div>
</template>

<script>
import { getModule } from "@/shared/api/modules-api";
import { createStack, runStack } from "@/shared/api/stacks-api";

import AppStackVariable from "./stack-variable.vue";
import { CREDENTIAL_VARIABLES } from "@/shared/constants/credentials";

export default {
  name: "AppStackCreation",

  components: {
    AppStackVariable
  },

  props: {
    moduleId: {
      type: String,
      required: true
    }
  },

  data() {
    return {
      module: null,
      stack: null,
      stacksVariablesValidated: false,
      credentialVariables: [],
      destroyAfterTimeOption: [
        { text: "8 Hours", value: "8" },
        { text: "12 Hours", value: "12" },
        { text: "24 Hours", value: "24" },
        { text: "Never", value: "-1" }
      ],
      googleCredentials: null,
      googleCredentialValue: null,
      credentialsFormValid: false
    };
  },

  watch: {
    googleCredentials(val) {
      if (!val) return;
      const fileReader = new FileReader();
      fileReader.onload = e => {
        console.log(e.target.result);
        this.googleCredentialValue = e.target.result.substr(29);
        console.log(
          this.googleCredentialValue && this.googleCredentialValue.length > 0
        );
        this.credentialsFormValid =
          this.googleCredentialValue && this.googleCredentialValue.length > 0;
      };
      fileReader.readAsDataURL(val);
    }
  },

  computed: {
    stackNameValid() {
      return typeof this.stack.name !== "undefined" && this.stack.name !== "";
    }
  },

  async created() {
    this.module = await getModule(this.moduleId);

    this.stack = {};
    this.stack.moduleId = this.module.id;
    this.stack.variableValues = {};
    this.stack.destroyAfterHours = "-1";
    let variables = this.module.variables.filter(
      item => item.name !== "credentials"
    );
    this.stack.variables = variables.map(variable => ({
      ...variable,
      value: variable.defaultValue || "",
      isValid: true
    }));
  },

  methods: {
    onDestroyChange() {
      console.log(this.stack.destroyType);
    },
    checkStackNameValidation() {
      return this.stackNameValid;
    },
    checkStackVariablesValidation() {
      return this.stack.variables.every(variable => variable.isValid);
    },
    stackVariablesMgmt() {
      this.stack.variableValues = {};
      this.stack.variables.forEach(variable => {
        this.stack.variableValues[variable.name] = variable.value;
      });
      if (this.credentialVariables && this.credentialVariables.length > 0) {
        this.credentialVariables.forEach(variable => {
          this.stack.variableValues[variable.name] = variable.value;
        });
      }
    },
    async saveStack() {
      this.stackVariablesMgmt();
      return createStack(this.stack);
    },
    async save() {
      const { id: stackId } = await this.saveStack();
      this.$router.push({ name: "stack_edition", params: { stackId } });
    },
    async saveAndRun() {
      if (this.module.mainProvider == "ONPREM") {
        const { id: stackId } = await this.saveStack();
        const { jobId } = await runStack(stackId);
        this.$router.push({ name: "job", params: { stackId, jobId } });
      } else {
        if (this.module.mainProvider == "AWS") {
          this.credentialVariables = Object.assign(
            [],
            CREDENTIAL_VARIABLES.AWS
          );
          this.credentialsFormValid = true;
        } else if (this.module.mainProvider == "AZURE") {
          this.credentialVariables = Object.assign(
            [],
            CREDENTIAL_VARIABLES.AZURE
          );
          this.credentialsFormValid = true;
        }
        this.$bvModal.show("update-credential-modal");
      }
    },
    handleCredentialsOk(bvModalEvt) {
      bvModalEvt.preventDefault();
      if (this.module.mainProvider == "GOOGLE") {
        this.credentialVariables.push({
          name: "credentials",
          value: this.googleCredentialValue,
          isValid: true
        });
      }
      let invalidItems = this.credentialVariables.filter(item => !item.isValid);
      if (invalidItems.length == 0) {
        this.handleCredentialsSubmit();
      }
    },
    async handleCredentialsSubmit() {
      const { id: stackId } = await this.saveStack();
      const { jobId } = await runStack(stackId);
      this.$router.push({ name: "job", params: { stackId, jobId } });
      this.$nextTick(() => {
        this.$bvModal.hide("update-credential-modal");
      });
    }
  }
};
</script>

<style scoped>
.specific-time-limit {
  margin-top: 30px;
}

.destroy-after-option {
  margin-top: 8px;
  margin-bottom: 10px;
}
</style>
