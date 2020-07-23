<template>
  <div v-if="stack">
    <div class="page_controls">
      <div class="row">
        <div class="col-md-6">
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
      </div>
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
            <div v-if="stack.enableTTL && stack.state === 'RUNNING'">
              <b-form-row
                class="metadata"
                v-if="!stack.destroySchedule && !stack.deploySchedule && stack.destroyType == 'T'"
              >
                <b-col cols="3">
                  <p>
                    Time Left :
                    <font-awesome-icon
                      :icon="['fa', 'edit']"
                      class="icon edit"
                      @click="$bvModal.show('update-destroy-time-modal')"
                    />
                  </p>
                </b-col>
                <b-col cols="6">
                  <span class="time-left" v-if="stack.destroyAfterHours == '-1'">Never</span>
                  <span class="time-left" v-if="stack.destroyAfterHours !== '-1'">{{ stack.nextDestroyScheduleTime | timeDuration}}</span>
                </b-col>
              </b-form-row>
              <b-form-row
                class="metadata"
                v-if="!stack.destroySchedule && !stack.deploySchedule && stack.destroyType == 'D'"
              >
                <b-col cols="3">
                  <p>
                    Next Schedule :
                  </p>
                </b-col>
                <b-col cols="6">
                  <span class="time-left">{{stack.nextDestroyScheduleTime | dateTime}} </span>
                  <font-awesome-icon
                      :icon="['fa', 'rocket']"
                      class="icon warning"
                    />
                </b-col>
              </b-form-row>
              <b-form-row
                class="metadata"
                v-if="stack.destroySchedule"
              >
                <b-col cols="3">
                  <p>
                    Next Schedule :
                  </p>
                </b-col>
                <b-col cols="6">
                  <span class="time-left">{{stack.nextDestroyScheduleTime | dateTime}} </span>
                  <font-awesome-icon
                      :icon="['fa', 'rocket']"
                      class="icon warning"
                    />
                </b-col>
              </b-form-row>
              <b-form-row
                class="metadata"
                v-if="stack.deploySchedule"
              >
                <b-col cols="3">
                  <p>
                    Next Schedule :
                  </p>
                </b-col>
                <b-col cols="6">
                  <span class="time-left">{{stack.nextDeployScheduleTime | dateTime}} </span>
                  <font-awesome-icon
                      :icon="['fa', 'rocket']"
                      class="icon edit"
                    />
                </b-col>
              </b-form-row>
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
            <b-form-row class="margin_bottom_10">
              <b-form-checkbox id="enableTTL" v-model="stack.enableTTL" name="enableTTL">
                <span id="enableTTLPop">Enable Schedule & TTL</span>
              </b-form-checkbox>
              <b-popover
                target="enableTTLPop"
                variant="danger"
                triggers="hover focus"
                placement="bottomright"
              >
                <template v-slot:title>Warning</template>
                Your credentials will be saved to peform TTL & Scheduling once enabled..!
              </b-popover>
            </b-form-row>
          </div>
        </div>
      </div>

      <app-stack-outputs :outputs="state.outputs || state.modules[0].outputs" />
    </div>

    <div class="row margin_bottom_30">
      <div class="col-md-6">
        <div class="column">
          <div class="block margin_bottom_30">
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
          <div class="block">
            <div class="block_head">
              <h2>Scheduling : {{stack.enableTTL ? 'ON' : 'OFF'}}</h2>
              <small>Scheduling allows you to deploy and destroy your cluster on a scheduled basis.</small>
            </div>
            <div class="block_content">
              <div
                class="small"
              >Specify cron expressions below (in UTC timezone, ex: 0 24 23 * * ? *):</div>
              <b-form-row class="align-items-center margin_top_10 margin_bottom_10">
                <b-col cols="4">
                  <b-form-checkbox
                    id="deploySchedule"
                    v-model="stack.deploySchedule"
                    name="deploySchedule"
                    @change="stack.changeInTTL=true;"
                    :disabled="!stack.enableTTL"
                  >Deploy Schedule</b-form-checkbox>
                </b-col>
                <b-col cols="6">
                  <b-form-group>
                    <b-input
                      id="deployScheduleExpression"
                      v-model="stack.deployScheduleExpression"
                      type="text"
                      class="form-control"
                      @change="stack.changeInTTL=true;"
                      :state="validateRegex(stack.deploySchedule, stack.deployScheduleExpression)"
                      :disabled="!stack.enableTTL || !stack.deploySchedule"
                    />
                    <!-- <b-form-invalid-feedback>Invalid cron expression syntax</b-form-invalid-feedback> -->
                  </b-form-group>
                </b-col>
              </b-form-row>
              <b-form-row class="align-items-center">
                <b-col cols="4">
                  <b-form-checkbox
                    id="destroySchedule"
                    v-model="stack.destroySchedule"
                    name="destroySchedule"
                    :disabled="!stack.enableTTL"
                    @change="onChangeScheduleCheck()"
                  >Destroy Schedule</b-form-checkbox>
                </b-col>
                <b-col cols="6">
                  <b-form-group>
                    <b-input
                      id="destroyScheduleExpression"
                      v-model="stack.destroyScheduleExpression"
                      type="text"
                      class="form-control"
                      @change="onChangeScheduleCheck()"
                      :state="validateRegex(stack.destroySchedule, stack.destroyScheduleExpression)"
                      :disabled="!stack.enableTTL || !stack.destroySchedule"
                    />
                    <!-- <b-form-invalid-feedback>Invalid cron expression syntax</b-form-invalid-feedback> -->
                  </b-form-group>
                </b-col>
              </b-form-row>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <app-job-history :jobs="jobs" @job-deleted="refreshJobs" />
      </div>
    </div>

    <b-modal
      id="update-destroy-time-modal"
      ref="modal"
      title="Update Destroy Time"
      okVariant="success"
      button-size="sm"
      @ok="handleTimeLeftOk"
    >
      <form ref="form" @submit.stop.prevent="handleTimeLeftSubmit">
        <b-form-group label="Cluster will be destroyed in">
          <b-form-row>
            <b-col cols="4">
              <b-form-radio-group
                id="radio-group-1"
                v-model="stack.destroyType"
                name="destroy-options"
                @change="stack.changeInTTL=true;"
              >
                <b-form-radio class="destroy-after-option" value="T">Destroy after</b-form-radio>
                <b-form-radio value="D">Specific time limit</b-form-radio>
              </b-form-radio-group>
            </b-col>
            <b-col cols="4" v-if="stack.destroyType == 'T'">
              <b-form-select
                v-model="stack.destroyAfterHours"
                :options="destroyAfterTimeOption"
                @change="stack.changeInTTL=true;"
              ></b-form-select>
            </b-col>
            <b-col cols="5" class="specific-time-limit" v-if="stack.destroyType == 'D'">
              <b-form-datepicker
                id="destroyAfterTime-datepicker"
                placeholder="Date"
                v-model="stack.destroyAfterDate"
                @change="stack.changeInTTL=true;"
              ></b-form-datepicker>
            </b-col>
            <b-col cols="3" class="specific-time-limit" v-if="stack.destroyType == 'D'">
              <b-form-timepicker
                v-model="stack.destroyAfterTime"
                placeholder="Time"
                locale="en"
                @change="stack.changeInTTL=true;"
              ></b-form-timepicker>
            </b-col>
          </b-form-row>
        </b-form-group>
      </form>
    </b-modal>

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
        <!-- <b-form-checkbox
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
        </b-popover>-->
      </form>
    </b-modal>
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
import { CREDENTIAL_VARIABLES } from "@/shared/constants/credentials";
import AppJobTimer from '@/pages/stacks/job/job-timer.vue';

export default {
  name: "AppStackEdition",

  components: {
    AppJobHistory,
    AppStackVariable,
    AppStackOutputs,
    AppUserBadge,
    AppJobTimer
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
    jobs: [],
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
  }),

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
      if (this.credentialVariables && this.credentialVariables.length > 0) {
        this.credentialVariables.forEach(variable => {
          this.stack.variableValues[variable.name] = variable.value;
        });
      }
      saveStack(this.stack)
        .then(() => {
          this.stack.changeInTTL = false;
          displayNotification(this, {
            variant: "success",
            message: "Cluster saved."
          });
        })
        .catch(({ message }) => {
          displayNotification(this, {
            variant: "info",
            message: `Error saving Cluster: ${message}`
          });
        });
    },
    async runStack() {
      if (this.module.mainProvider == "ONPREM") {
        // ask for confirmation
        const message = "Modifications must be saved before. Continue?";
        if (
          await displayConfirmDialog(this, { title: "Run request", message })
        ) {
          await this.saveStack();
          const { jobId } = await runStack(this.stack.id);
          this.$router.push({ name: "job", params: { jobId } });
        }
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
    },
    validateRegex(isEnabled, value) {
      if (!isEnabled) {
        return true;
      } else {
        return /((((\d+,)+\d+|(\d+(\/|-)\d+)|\d+|\*) ?){5,7})/g.test(value);
      }
    },
    handleTimeLeftOk(bvModalEvt) {
      bvModalEvt.preventDefault();
      this.handleTimeLeftSubmit();
    },
    handleTimeLeftSubmit() {
      this.saveStack();
      this.$nextTick(() => {
        this.$bvModal.hide("update-destroy-time-modal");
      });
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
      console.log(this.credentialVariables);
      this.saveStack();
      const { jobId } = await runStack(this.stack.id);
      this.$router.push({ name: "job", params: { jobId } });
      this.$nextTick(() => {
        this.$bvModal.hide("update-credential-modal");
      });
    }
  },
  onChangeScheduleCheck() {
    this.stack.changeInTTL=true;
    if (!this.stack.deploySchedule && !this.stack.destroySchedule) {
        this.stack.destroyType = "T";
        this.stack.destroyAfterHours = "-1";
      }
  }
};
</script>

<style scoped>
.align-items-center {
  align-items: center;
}

.edit,
.time-left {
  color: #3273dc;
  cursor: pointer;
}

.warning {
  color: #bd2130;
}

.specific-time-limit {
  margin-top: 30px;
}

.destroy-after-option {
  margin-top: 8px;
  margin-bottom: 10px;
}

.block-content-modal {
  padding: 18px 25px 15px;
  width: 100%;
}
</style>
