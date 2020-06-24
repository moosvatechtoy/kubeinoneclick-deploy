<template>
  <div>
    <div class="row">
      <div class="col-md-6 col-lg-4 margin_bottom_30">
        <b-overlay :show="loading">
          <app-dashboard-widget
            class="prucian-blue-bg"
            text="Providers"
            :value="summary.modulesCount"
            icon="object-group"
            :to="{ name: 'modules' }"
          />
        </b-overlay>
      </div>
      <div class="col-md-6 col-lg-4 margin_bottom_30">
        <b-overlay :show="loading">
          <app-dashboard-widget
            class="blue2_bg"
            text="Provisioners"
            :value="summary.stacksCount"
            icon="layer-group"
            :to="{ name : 'stacks' }"
          />
        </b-overlay>
      </div>
      <div class="col-md-6 col-lg-4 margin_bottom_30">
        <b-overlay :show="loading">
          <app-dashboard-widget
            class="green-bg"
            text="Running Provisioners"
            :value="summary.runningStacksCount"
            icon="caret-square-up"
            :to="{ name : 'stacks' }"
          />
        </b-overlay>
      </div>
      <div class="col-md-6 col-lg-4 margin_bottom_30" v-for="moduleCount in summary.counts" :key="moduleCount.name">
        <b-overlay :show="loading">
          <app-dashboard-widget
            class="blue2_bg"
            :text="moduleCount.name + ' Provisioners'"
            :value="moduleCount.counts"
            icon="layer-group"
            :to="{ name: 'stacks', params: { moduleParam: moduleCount.name} }"
          />
        </b-overlay>
      </div>
      
    </div>
    <div v-if="isWithoutTeam" class="center">
      <div class="error_page">
        <div class="center">
          <img alt="#" class="img-responsive" src="@/assets/images/waving-hand-sign.png" />
        </div>
        <br />
        <h3>Hi there !</h3>
        <p>It seems that you're not a member of a team yet. Ask your admin to add you a team !</p>
        <p>Until then, you'll only be able to use public modules.</p>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import AppDashboardWidget from "@/pages/dashboard/dashboard-widget.vue";
import { getSummary } from "@/shared/api/dashboard-api";
import { getModules } from "@/shared/api/modules-api";

export default {
  name: "AppDashboard",
  components: {
    AppDashboardWidget
  },
  data: () => ({
    loading: true,
    modules: [],
    summary: { modulesCount: 0, runningStacksCount: 0, toUpdateStacksCount: 0 }
  }),
  computed: {
    ...mapState("session", ["user"]),
    isWithoutTeam() {
      return !this.user.admin && !this.user.team;
    }
  },
  async created() {
    this.summary = await getSummary();
    this.modules = await getModules();
    this.summary.counts = this.computeCount(this.summary, this.modules);
    this.loading = false;
  },
  methods: {
    computeCount(summary, modules) {
      let counts = [];
      if (summary.stacks) {
        for (let module of modules) {
          let count = summary.stacks.filter(
            item => item.moduleId === module.id
          );
          counts.push({name: module.name, counts: count.length});
        }
      }
      return counts;
    }
  }
};
</script>
