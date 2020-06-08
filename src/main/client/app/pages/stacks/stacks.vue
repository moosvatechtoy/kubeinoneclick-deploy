<template>
  <div>
    <b-form-row>
      <b-col cols="4">
        <b-form-group description="The Template to create Provisioner">
        <vue-multiselect
          v-model="moduleId"
          label="name"
          track-by="id"
          searchable
          placeholder="Select Template"
          :options="modules"
          :show-labels="false"
          @select="onModuleSelect"
        />
        </b-form-group>
      </b-col>
      <b-col cols="4">
        <b-button
          title="Add Provisioner"
          variant="success"
          class="mb-4"
          :disabled="!moduleId"
          @click="createStack()"
        >
          <font-awesome-icon icon="plus" />Add Provisioner
        </b-button>
      </b-col>
    </b-form-row>

    <b-card-group columns>
      <b-card
        v-for="stack in stacks"
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
  </div>
</template>

<script>
import { getStacks } from "@/shared/api/stacks-api";
import { getModules } from "@/shared/api/modules-api";

export default {
  name: "AppStacks",
  data: () => ({
    stacks: [],
    modules: [],
    moduleId: "",
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
    this.stacks = await getStacks();
    this.modules = await getModules();
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
    onModuleSelect(module) {
      this.moduleId = module.id;
    }
  }
};
</script>

<style scoped>
</style>
