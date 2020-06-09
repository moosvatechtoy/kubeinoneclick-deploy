<template>
  <div>
    <b-form-row>
      <b-col cols="8">
        <b-button
          title="Add Provisioner"
          variant="success"
          class="mb-4"
          v-b-modal.modal-prevent-closing
        >
          <font-awesome-icon icon="plus" />Add Provisioner
        </b-button>
      </b-col>
      <b-col cols="2">
        <vue-multiselect
          v-model="provider"
          id="provider-input"
          label="name"
          track-by="id"
          searchable
          placeholder="Select Provider"
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
          :options="['NEW', 'RUNNING']"
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
          title="Edit this Provisioner"
          variant="primary"
          class="mr-1"
        >
          <font-awesome-icon icon="edit" />
        </b-button>
      </b-card>
    </b-card-group>

    <b-modal
      id="modal-prevent-closing"
      ref="modal"
      title="Select Provider"
      @show="resetModal"
      @hidden="resetModal"
      @ok="handleOk"
    >
      <form ref="form" @submit.stop.prevent="handleSubmit">
        <b-form-group
          :state="templateState"
          description="The Provider to create Provisioner"
          label-for="template-input"
          invalid-feedback="Provider is required to create Provisioner"
        >
        <vue-multiselect
          v-model="moduleId"
          id="template-input"
          label="name"
          track-by="id"
          searchable
          placeholder="Select Provider"
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
import { getStacks } from "@/shared/api/stacks-api";
import { getModules } from "@/shared/api/modules-api";

export default {
  name: "AppStacks",
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
        tooltip: "Your provisioner is new and has not been started yet.",
        icon: "star-of-life",
        text: "new"
      },
      RUNNING: {
        variant: "primary",
        tooltip: "Your provisioner is up and running !",
        icon: ["far", "check-square"],
        text: "running"
      },
      TO_UPDATE: {
        variant: "warning",
        tooltip: "Your Provisioner needs an update !",
        icon: "upload",
        text: "to update"
      },
      STOPPED: {
        variant: "danger",
        tooltip: "Your Provisioner has been stopped.",
        icon: "stop-circle",
        text: "stopped"
      }
    }
  }),
  async created() {
    this.modules = await getModules();
    this.stacks = await getStacks();
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
