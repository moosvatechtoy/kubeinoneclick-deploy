<template>
  <div v-if="stack">
    <div class="page_controls">
      <b-button :disabled="!formValid" variant="primary" class="mr-1" @click="saveStack">
        <font-awesome-icon icon="save" />Save
      </b-button>
      <b-button v-if="stack.state === 'FAILED'" variant="danger" class="mr-1" @click="retryJob">
        <font-awesome-icon icon="redo" />Retry
      </b-button>
      <b-button
        v-if="stack.state === 'NEW' || stack.state === 'STOPPED'"
        :disabled="!formValid"
        variant="primary"
        class="mr-1"
        @click="runStack"
      >
        <font-awesome-icon icon="rocket" />Run
      </b-button>
      <b-button
        v-if="stack.state === 'TO_UPDATE'"
        :disabled="!formValid"
        variant="warning"
        class="mr-1"
        @click="runStack"
      >
        <font-awesome-icon icon="upload" />Update
      </b-button>
      <b-button
        v-if="stack.state === 'RUNNING' || stack.state === 'TO_UPDATE'"
        :disabled="!formValid"
        variant="danger"
        class="mr-1"
        @click="stopStack"
      >
        <font-awesome-icon icon="stop-circle" />Destroy
      </b-button>
    </div>

    <div class="row margin_bottom_30">
      <div class="col-md-6">
        <div class="block">
          <div class="block_head">
            <h2>Cluster {{ stack.name }}</h2>
            <small>{{ stack.description }}</small>
            <div class="metadata">
              <p>
                Template :
                <router-link
                  :to="{ name: 'module_description', params: { moduleId: stack.moduleId }}"
                >{{ module.name }}</router-link>
              </p>
            </div>
            <div class="metadata">
              <p>
                Published
                <b>{{ stack.createdAt | dateTimeLong }}</b> by
                <app-user-badge :user="stack.createdBy" />
              </p>
              <p v-if="stack.updatedBy">
                Last modified
                <b>{{ stack.updatedAt | dateTimeLong }}</b> by
                <app-user-badge :user="stack.updatedBy" />
              </p>
              <p v-if="stack.estimatedRunningCost">
                Estimated total running cost :
                <b-badge variant="info">{{ stack.estimatedRunningCost }} $</b-badge>
              </p>
            </div>
            <h2>
              <b-badge
                v-if="stack.state === 'NEW'"
                variant="success"
                pill
                data-toggle="tooltip"
                title="Your Cluster is new and has not been started yet."
              >
                <i class="fas fa-star-of-life" /> new
              </b-badge>
              <b-badge
                v-if="stack.state === 'RUNNING'"
                variant="primary"
                pill
                data-toggle="tooltip"
                title="Your Cluster is up and running !"
              >
                <i class="far fa-check-square" /> running
              </b-badge>
              <b-badge
                v-if="stack.state === 'TO_UPDATE'"
                variant="warning"
                pill
                data-toggle="tooltip"
                title="Your Cluster needs an update !"
              >
                <i class="fas fa-upload" /> to update
              </b-badge>
              <b-badge
                v-if="stack.state === 'STOPPED'"
                variant="danger"
                pill
                data-toggle="tooltip"
                title="Your Cluster has been stopped."
              >
                <i class="fas fa-stop-circle" /> stopped
              </b-badge>
              <b-badge
                v-if="stack.state === 'FAILED'"
                variant="danger"
                pill
                data-toggle="tooltip"
                title="Your Cluster has been failed."
              >
                <i class="fas fa-stop-circle" /> failed
              </b-badge>
            </h2>
          </div>
          <div class="block_content">
            <b-form-group label="Name">
              <b-input
                id="stack.name"
                v-model="stack.name"
                :state="stack.name !== ''"
                aria-describedby="input-live-help input-live-feedback"
                trim
              />
              <b-form-invalid-feedback id="input-live-feedback">This field is mandatory.</b-form-invalid-feedback>
              <b-form-text id="input-live-help">This is the name of your Cluster.</b-form-text>
            </b-form-group>
            <b-form-invalid-feedback id="input-live-feedback">This field is mandatory</b-form-invalid-feedback>
            <div class="form-group">
              <label for="description">Description</label>
              <b-input
                id="description"
                v-model="stack.description"
                type="text"
                class="form-control"
              />
            </div>
          </div>
        </div>
      </div>

      <app-stack-outputs :outputs="state.outputs || state.modules[0].outputs" />
    </div>

    <div class="row margin_bottom_30">
      <div class="col-md-6">
        <div class="block">
          <div class="block_head">
            <h2>Variable values</h2>
            <small>This is the configuration of your profile's variables !</small>
          </div>
          <div v-if="stack.variables" class="block_content">
            <app-stack-variable
              v-for="variable in editableVars"
              :key="variable.name"
              v-model="variable.value"
              v-bind="moduleVar(variable.name)"
              @valid="(isValid) => variable.isValid = isValid"
            />
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <app-job-history :jobs="jobs" @job-deleted="refreshJobs" />
      </div>
    </div>
  </div>
</template>

<script>
import {
  destroyStack,
  getStack,
  runStack,
  saveStack
} from "@/shared/api/stacks-api";
import { getModule } from "@/shared/api/modules-api";
import { getState } from "@/shared/api/state-api";
import { retryJob } from "@/shared/api/jobs-api";

import AppStackVariable from "@/pages/stacks/stack-variable.vue";
import AppStackOutputs from "@/pages/stacks/stack-outputs.vue";
import AppUserBadge from "@/pages/users/user-badge.vue";
import AppJobHistory from "@/pages/stacks/job/job-history.vue";
import {
  displayConfirmDialog,
  displayNotification
} from "@/shared/services/modal-service";
import { getJobs } from "@/shared/api/jobs-api";

export default {
  name: "AppStackEdition",

  components: {
    AppJobHistory,
    AppStackVariable,
    AppStackOutputs,
    AppUserBadge
  },

  props: {
    stackId: {
      type: String,
      required: true
    }
  },

  data: () => ({
    stack: null,
    module: null,
    state: { outputs: {} },
    jobs: []
  }),

  computed: {
    formValid() {
      return (
        this.stack.variables.every(variable => variable.isValid) &&
        this.stack.name.trim() !== ""
      );
    },
    editableVars() {
      return this.stack.variables.filter(variable => variable.editable);
    }
  },

  async created() {
    const stack = await getStack(this.stackId);
    this.module = await getModule(stack.moduleId);
    try {
      this.state = await getState(this.stackId);
    } catch (e) {
      // unable to load state info, (stack never run), keeping default empty data
    }
    try {
      this.jobs = await getJobs(this.stackId);
    } catch (e) {
      // unable to load job info, (stack never run), keeping default empty data
    }
    stack.variables = this.module.variables.map(variable => ({
      ...variable,
      value: stack.variableValues[variable.name],
      isValid: true
    }));

    this.stack = stack;
  },

  methods: {
    retryJob() {
      if (this.jobs && this.jobs.length > 0) {
        this.callRetry(this.jobs[0].id);
      }
    },
    async callRetry(jobId) {
      try {
        await retryJob(jobId);
        displayNotification(this, { message: "Job retried.", variant: "info" });
        this.$emit("job-retried", jobId);
      } catch (e) {
        displayNotification(this, {
          message: "Unable to retry job.",
          variant: "danger"
        });
      }
    },
    moduleVar(name) {
      return this.module.variables.find(variable => variable.name === name);
    },
    saveStack() {
      this.stack.variableValues = {};
      this.stack.variables.forEach(variable => {
        this.stack.variableValues[variable.name] = variable.value;
      });
      saveStack(this.stack)
        .then(() =>
          displayNotification(this, {
            variant: "success",
            message: "Cluster saved."
          })
        )
        .catch(({ message }) => {
          displayNotification(this, {
            variant: "info",
            message: `Error saving Cluster: ${message}`
          });
        });
    },
    async runStack() {
      // ask for confirmation
      const message = "Modifications must be saved before. Continue?";
      if (await displayConfirmDialog(this, { title: "Run request", message })) {
        await this.saveStack();
        const { jobId } = await runStack(this.stack.id);
        this.$router.push({ name: "job", params: { jobId } });
      }
    },
    async stopStack() {
      // ask for confirmation
      const message =
        "This will completely stop the clusters, and destroy all created resources. Continue?";
      if (
        await displayConfirmDialog(this, { title: "Stop request", message })
      ) {
        const { jobId } = await destroyStack(this.stack.id);
        this.$router.push({ name: "job", params: { jobId } });
      }
    },
    async refreshJobs() {
      this.jobs = await getJobs(this.stackId);
    }
  }
};
</script>

<style scoped>
</style>
