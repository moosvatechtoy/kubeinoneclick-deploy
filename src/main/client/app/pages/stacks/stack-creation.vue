<template>
  <div
    v-if="module"
    class="block"
  >
    <form-wizard
      title="Run a new Cluster"
      subtitle="Follow the steps to start a new Cluster"
      color="#00ab94"
      error-color="#dc3545"
    >
      <tab-content
        title="Cluster"
        :before-change="checkStackNameValidation"
      >
        <h4>{{ module.name }}</h4>
        <p>{{ module.description }}</p>

        <hr>

        <div class="form-group">
          <b-form-group
            label="Name"
            description="The name of your Cluster"
          >
            <b-input
              v-model="stack.name"
              :state="stackNameValid"
              autofocus
            />
            <b-form-invalid-feedback>This field is mandatory</b-form-invalid-feedback>
          </b-form-group>
          <b-form-group
            label="Description"
            description="The description of your Cluster"
          >
            <b-input v-model="stack.description" />
          </b-form-group>
        </div>
      </tab-content>
      <tab-content
        title="Variables"
        :before-change="checkStackVariablesValidation"
      >
        <h2>Input variables</h2>
        <app-stack-variable
          v-for="variable in stack.variables"
          :key="variable.name"
          v-model="variable.value"
          v-bind="variable"
          @valid="(isValid) => variable.isValid = isValid"
        />
      </tab-content>
      <tab-content title="Start">
        <h2>Run</h2>
        <p>Save your Cluster or run it !</p>
      </tab-content>

      <!-- customizing save button -->
      <template
        slot="finish"
        slot-scope="props"
      >
        <wizard-button
          v-if="props.isLastStep"
          :style="props.fillButtonStyle"
          class="wizard-footer-right finish-button"
          @click.native="save"
        >
          <font-awesome-icon icon="save" /> Save
        </wizard-button>
      </template>

      <!-- add run button -->
      <template
        slot="custom-buttons-right"
        slot-scope="props"
      >
        <wizard-button
          v-if="props.isLastStep"
          :style="props.fillButtonStyle"
          class="wizard-footer-right finish-button ml-3"
          @click.native="saveAndRun"
        >
          <font-awesome-icon icon="rocket" /> Save and run
        </wizard-button>
      </template>
    </form-wizard>
  </div>
</template>

<script>
  import { getModule } from '@/shared/api/modules-api';
  import { createStack, runStack } from '@/shared/api/stacks-api';

  import AppStackVariable from './stack-variable.vue';

  export default {
    name: 'AppStackCreation',

    components: {
      AppStackVariable,
    },

    props: {
      moduleId: {
        type: String,
        required: true,
      },
    },

    data() {
      return {
        module: null,
        stack: null,
        stacksVariablesValidated: false,
      };
    },

    computed: {
      stackNameValid() {
        return typeof this.stack.name !== 'undefined' && this.stack.name !== '';
      },
    },

    async created() {
      this.module = await getModule(this.moduleId);

      this.stack = {};
      this.stack.moduleId = this.module.id;
      this.stack.variableValues = {};
      let variables = this.module.variables.filter(item => item.name !== 'credentials');
      this.stack.variables = variables.map((variable) => ({
        ...variable,
        value: variable.defaultValue || '',
        isValid: true,
      }));
    },

    methods: {
      checkStackNameValidation() {
        return this.stackNameValid;
      },
      checkStackVariablesValidation() {
        return this.stack.variables.every((variable) => variable.isValid);
      },
      stackVariablesMgmt() {
        this.stack.variableValues = {};
        this.stack.variables.forEach((variable) => {
          this.stack.variableValues[variable.name] = variable.value;
        });
        let credentialVariables = this.module.variables.filter(item => item.name === 'credentials');
        if (credentialVariables && credentialVariables.length > 0) {
          let variable = {
            ...credentialVariables[0],
            value: credentialVariables[0].defaultValue,
            isValid: true
          };
          this.stack.variables.push(variable);
          this.stack.variableValues['credentials'] = credentialVariables[0].defaultValue;
        }
      },
      async saveStack() {
        this.stackVariablesMgmt();
        return createStack(this.stack);
      },
      async save() {
        const { id: stackId } = await this.saveStack();
        this.$router.push({ name: 'stack_edition', params: { stackId } });
      },
      async saveAndRun() {
        const { id: stackId } = await this.saveStack();
        const { jobId } = await runStack(stackId);
        this.$router.push({ name: 'job', params: { stackId, jobId } });
      },
    },
  };
</script>

<style scoped>

</style>
