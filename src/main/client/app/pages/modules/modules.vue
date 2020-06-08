<template>
  <div>
    <b-form-row>
      <b-col cols="10">
        <b-button
          :to="{ name: 'module', params: { moduleId: 'ADD' }}"
          title="Add Template"
          variant="success"
          class="mb-4"
        >
          <font-awesome-icon icon="plus" />Add Template
        </b-button>
      </b-col>
      <b-col cols="2">
        <vue-multiselect
          v-model="provider"
          searchable
          placeholder="Select Provider"
          :show-labels="false"
          :options="['', 'AWS','AZURE', 'GOOGLE', 'ONPREM']"
          @select="onProviderSelect"
        />
      </b-col>
    </b-form-row>

    <b-card-group columns>
      <b-card v-for="module in filteredModules" :key="module.id" :title="module.name" class="moduleCard">
        <b-card-text>
          <app-cli-badge
            :cli="module.terraformImage"
            badge-style="flat-square"
            style="margin-bottom: .75rem"
          />
          <p>{{ module.description }}</p>

          <!-- <p v-if="module.estimatedMonthlyCost">
            Estimated monthly cost :
            <b-badge variant="info">{{ module.estimatedMonthlyCost }} $</b-badge>
          </p> -->
        </b-card-text>

        <b-button
          :to="{ name: 'module', params: { moduleId: module.id }}"
          title="Edit this Template"
          variant="primary"
          class="mr-1"
        >
          <font-awesome-icon icon="edit" />
        </b-button>

        <b-button
          :to="{ name: 'module_description', params: { moduleId: module.id }}"
          title="Detail of this Template"
          variant="primary"
          class="mr-1"
        >
          <font-awesome-icon icon="info" />
        </b-button>

        <b-button
          title="Run this Template"
          variant="primary"
          class="mr-1"
          @click="createStack(module.id)"
        >
          <font-awesome-icon icon="rocket" />
        </b-button>

        <b-button
          title="Delete this Template"
          variant="danger"
          class="mr-1"
          @click="createStack(module.id)"
        >
          <font-awesome-icon :icon="['far', 'trash-alt']" />
        </b-button>
      </b-card>
    </b-card-group>
  </div>
</template>

<script>
import { getModules } from "@/shared/api/modules-api";

import { AppCliBadge } from "@/shared/components";

export default {
  name: "AppModules",
  components: {
    AppCliBadge
  },

  data: function data() {
    return {
      filteredModules: [],
      modules: [],
      provider: "AWS"
    };
  },

  async created() {
    this.modules = await getModules();
    this.filteredModules = this.modules.filter(item => item.mainProvider === 'AWS');
  },

  methods: {
    createStack(moduleId) {
      this.$router.push({
        name: "stack_creation",
        params: {
          moduleId
        }
      });
    },
    onProviderSelect(provider) {
      if (provider !== '') {
        this.filteredModules = this.modules.filter(item => item.mainProvider === provider);
      } else {
        this.filteredModules = this.modules;
      }
    }
  }
};
</script>
