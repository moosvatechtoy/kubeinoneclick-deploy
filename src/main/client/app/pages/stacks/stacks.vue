<template>
  <div>
    <b-form-row>
      <b-col cols="7">
        <b-button
          title="Add Cluster"
          variant="success"
          class="mb-4"
          v-b-modal.modal-prevent-closing
        >
          <font-awesome-icon icon="plus" />Add Cluster
        </b-button>
      </b-col>
      <b-col cols="3">
        <vue-multiselect
          v-model="provider"
          id="provider-input"
          label="name"
          track-by="id"
          searchable
          placeholder="Select Template"
          :options="modules"
          :show-labels="false"
          @select="onProviderSelect"
          @Remove="onProviderRemove"
        />
      </b-col>
      <b-col cols="2">
        <vue-multiselect
          v-model="status"
          searchable
          placeholder="Select Status"
          :show-labels="false"
          :options="['', 'NEW', 'RUNNING', 'STOPPED']"
          @select="onStatusSelect"
        />
      </b-col>
    </b-form-row>

    <b-card-group columns>
      <b-card
        v-for="stack in filteredStacks"
        :key="stack.id"
        :title="stack.name"
        :sub-title="stack.description"
      >
        <b-card-text>
          <b-badge :id="'badge-' + stack.id" pill :variant="states[stack.state].variant">
            <font-awesome-icon :icon="states[stack.state].icon" />
            &nbsp;{{ states[stack.state].text }}
          </b-badge>
          <b-tooltip :target="'badge-' + stack.id">{{ states[stack.state].tooltip }}</b-tooltip>
        </b-card-text>

        <b-button
          :to="{ name: 'stack_edition', params: { stackId: stack.id }}"
          title="Edit this Cluster"
          variant="primary"
          class="mr-1"
        >
          <font-awesome-icon icon="edit" />
        </b-button>
        <b-button
          title="Delete this Cluster"
          variant="danger"
          class="mr-1"
          @click="deleteStack(stack.id)"
        >
          <font-awesome-icon :icon="['far', 'trash-alt']" />
        </b-button>
      </b-card>
    </b-card-group>

    <b-modal
      id="modal-prevent-closing"
      ref="modal"
      title="Select Template"
      okVariant="success"
      @show="resetModal"
      @hidden="resetModal"
      @ok="handleOk"
    >
      <form ref="form" @submit.stop.prevent="handleSubmit">
        <b-form-group
          :state="templateState"
          description="The Template to create Cluster"
          label-for="template-input"
          invalid-feedback="Template is required to create Cluster"
        >
        <vue-multiselect
          v-model="moduleId"
          id="template-input"
          label="name"
          track-by="id"
          searchable
          placeholder="Select Template"
          :options="modules"
          :show-labels="false"
          :state="templateState"
          required
        />
        </b-form-group>
      </form>
    </b-modal>
  </div>
</template>

<script>
import { getStacks, deleteStack } from "@/shared/api/stacks-api";
import { displayNotification } from "@/shared/services/modal-service";
import { getModules } from "@/shared/api/modules-api";

export default {
  name: "AppStacks",
  props: {
      moduleParam: {
        type: String,
        required: false
      },
      clusterStatus: {
        type: String,
        required: false
      }
    },
  data: () => ({
    templateState: null,
    stacks: [],
    filteredStacks: [],
    modules: [],
    moduleId: "",
    provider: '',
    status: '',
    states: {
      NEW: {
        variant: "success",
        tooltip: "Your Cluster is new and has not been started yet.",
        icon: "star-of-life",
        text: "new"
      },
      RUNNING: {
        variant: "primary",
        tooltip: "Your Cluster is up and running !",
        icon: ["far", "check-square"],
        text: "running"
      },
      TO_UPDATE: {
        variant: "warning",
        tooltip: "Your Cluster needs an update !",
        icon: "upload",
        text: "to update"
      },
      STOPPED: {
        variant: "danger",
        tooltip: "Your Cluster has been stopped.",
        icon: "stop-circle",
        text: "stopped"
      }
    }
  }),
  async created() {
    this.modules = await getModules();
    this.stacks = await getStacks();
    if (this.moduleParam) {
      let moduleFiltered = this.modules.filter(item => item.name === this.moduleParam);
      if (moduleFiltered && moduleFiltered.length > 0) {
        this.provider = moduleFiltered[0];
      }
    }
    if (this.clusterStatus) {
      this.status = this.clusterStatus;
    }
    this.onProviderSelect(this.provider);
  },

  methods: {
    createStack() {
      let module = this.moduleId.id;
      this.$router.push({
        name: "stack_creation",
        params: {
           moduleId:module
        }
      });
    },
    async deleteStack(stackId) {
      await deleteStack(stackId)
          .then(() => {
            displayNotification(this, {
              message: "Stack deleted",
              variant: "success"
            });
            let index = this.stacks.findIndex(item => item.id === stackId);
            this.stacks.splice(index, 1);
          })
          .catch(({ error, message }) =>
            displayNotification(this, {
              title: error,
              message,
              variant: "danger"
            })
          );
    },
    onProviderSelect(provider) {
      this.filterByProviderAndStatus(provider, this.status)
    },
    onStatusSelect(status) {
      this.filterByProviderAndStatus(this.provider, status)
    },
    filterByProviderAndStatus(provider, status) {
      let items = this.stacks;
      if (provider !== '') { 
        items = items.filter(item => item.moduleId === provider.id);
      }

      if (status !== '') { 
        items = items.filter(item => item.state === status);
      }
      this.filteredStacks = items;
    },
    onProviderRemove(provider) {
      this.filteredStacks = this.stacks;
    },
    checkFormValidity() {
        const valid = this.moduleId !== '';
        this.templateState = valid;
        return valid;
      },
      resetModal() {
        this.templateState = null;
        this.moduleId = '';
      },
      handleOk(bvModalEvt) {
        bvModalEvt.preventDefault();
        this.handleSubmit();
      },
      handleSubmit() {
        if (!this.checkFormValidity()) {
          return;
        } else {
          this.createStack();
        }
        this.$nextTick(() => {
          this.$bvModal.hide('modal-prevent-closing')
        })
      }
  }
};
</script>

<style scoped>
</style>
