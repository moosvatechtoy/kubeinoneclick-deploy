<template>
  <div>
    <b-form-row>
      <b-col cols="10">
        <b-button
          :to="{ name: 'module', params: { moduleId: 'ADD' }}"
          title="Add Configuration"
          variant="success"
          class="mb-4"
        >
          <font-awesome-icon icon="plus" />Add Configuration
        </b-button>
      </b-col>
    </b-form-row>

    <b-card-group columns>
      <b-card v-for="module in modules" :key="module.id" :title="module.name" class="moduleCard">
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
          </p>-->
        </b-card-text>

        <b-button
          :to="{ name: 'module', params: { moduleId: module.id }}"
          title="Edit this Configuration"
          variant="primary"
          class="mr-1"
        >
          <font-awesome-icon icon="edit" />
        </b-button>

        <b-button
          :to="{ name: 'module_description', params: { moduleId: module.id }}"
          title="Detail of this Configuration"
          variant="primary"
          class="mr-1"
        >
          <font-awesome-icon icon="info" />
        </b-button>

        <b-button
          title="Run this Configuration"
          variant="primary"
          class="mr-1"
          @click="createStack(module.id)"
        >
          <font-awesome-icon icon="rocket" />
        </b-button>

        <b-button
          title="Delete this Configuration"
          variant="danger"
          class="mr-1"
          @click="deleteModule(module.id)"
        >
          <font-awesome-icon :icon="['far', 'trash-alt']" />
        </b-button>
      </b-card>
    </b-card-group>
  </div>
</template>

<script>
import { getModules, deleteModule } from "@/shared/api/modules-api";
import { displayNotification } from "@/shared/services/modal-service";
import { AppCliBadge } from "@/shared/components";

export default {
  name: "AppModules",
  components: {
    AppCliBadge
  },

  data: function data() {
    return {
      modules: []
    };
  },

  async created() {
    this.modules = await getModules();
  },

  methods: {
    async deleteModule(moduleId) {
      await deleteModule(moduleId)
        .then(() => {
          displayNotification(this, {
            message: "Module deleted",
            variant: "success"
          });
          let index = this.modules.findIndex(item => item.id === moduleId);
          this.modules.splice(index, 1);
        })
        .catch(({ error, message }) =>
          displayNotification(this, {
            title: error,
            message,
            variant: "danger"
          })
        );
    },
    createStack(moduleId) {
      this.$router.push({
        name: "stack_creation",
        params: {
          moduleId
        }
      });
    }
  }
};
</script>
