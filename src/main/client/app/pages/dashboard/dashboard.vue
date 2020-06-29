<template>
  <div>
    <div class="row">
      <div class="col-md-6 col-lg-4 margin_bottom_30">
        <b-overlay :show="loading">
          <app-dashboard-widget
            class="prucian-blue-bg"
            text="Templates"
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
            text="Clusters"
            :value="summary.stacksCount"
            icon="layer-group"
            :to="{ name : 'stacks' }"
          />
        </b-overlay>
      </div>
      <div class="col-md-6 col-lg-4 margin_bottom_30">
        <b-overlay :show="loading">
          <app-dashboard-widget
            class="green_bg"
            text="Running Clusters"
            :value="summary.runningStacksCount"
            icon="caret-square-up"
            :to="{ name : 'stacks' }"
          />
        </b-overlay>
      </div>
    </div>
    <div class="row">
      <div class="col-md-6 col-lg-6 margin_bottom_30" v-if="stackbyStatusAndProviderOptions">
        <vue-highcharts :options="stackbyStatusAndProviderOptions" ref="barChart"></vue-highcharts>
      </div>
      <div class="col-md-6 col-lg-6 margin_bottom_30" v-if="runningStackOptions">
        <vue-highcharts :options="runningStackOptions" ref="pie"></vue-highcharts>
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
import Highcharts from "highcharts";
import More from "highcharts/highcharts-more";
More(Highcharts);
import VueHighcharts from "vue2-highcharts";
import threeD from "highcharts/highcharts-3d";
threeD(Highcharts);

Highcharts.setOptions({
  colors: Highcharts.map(Highcharts.getOptions().colors, color => {
    return {
      radialGradient: {
        cx: 0.5,
        cy: 0.3,
        r: 0.7
      },
      stops: [
        [0, color],
        [
          1,
          Highcharts.color(color)
            .brighten(-0.3)
            .get("rgb")
        ]
      ]
    };
  })
});

export default {
  name: "AppDashboard",
  components: {
    AppDashboardWidget,
    VueHighcharts
  },
  data: () => ({
    loading: true,
    modules: [],
    summary: { modulesCount: 0, runningStacksCount: 0, toUpdateStacksCount: 0 },
    stackbyStatusAndProviderOptions: false,
    runningStackOptions: false,
    Highcharts
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
    this.summary.runningStackOptions = this.getRunningStacksByProvider(
      this.summary,
      this.modules
    );
    this.summary.stackbyStatusAndProviderOptions = this.getStacksByStatusAndProvider(
      this.summary,
      this.modules
    );
    this.stackbyStatusAndProviderOptions = {
      chart: {
        type: "column",
        marginBottom: 90
      },
      title: {
        text: "Clusters By Status & Template"
      },
      xAxis: {
        categories: this.summary.stackbyStatusAndProviderOptions.categories,
        title: {
          text: null
        }
      },
      yAxis: {
        min: 0,
        title: {
          text: "Clusters Count",
          align: "high"
        },
        labels: {
          overflow: "justify"
        }
      },
      tooltip: {
        valueSuffix: ""
      },
      plotOptions: {
        bar: {
          dataLabels: {
            enabled: true
          }
        },
        series: {
          cursor: "pointer",
          point: {
            events: {
              click: event => {
                this.$router.push({
                  name: "stacks",
                  params: {
                    moduleParam: event.point.category,
                    clusterStatus: event.point.series.name.toUpperCase()
                  }
                });
              }
            }
          }
        }
      },
      legend: {
        align: "center",
        verticalAlign: "bottom",
        x: 0,
        y: 0,
        floating: true,
        borderWidth: 1,
        backgroundColor:
          (Highcharts.theme && Highcharts.theme.legendBackgroundColor) ||
          "#FFFFFF",
        shadow: true
      },
      colors: ["#2f7ed8", "#8bbc21", '#eb720c', "#C70000"],
      credits: {
        enabled: false
      },
      series: this.summary.stackbyStatusAndProviderOptions.series
    };
    this.runningStackOptions = {
      chart: {
        type: "pie",
        options3d: {
          enabled: true,
          alpha: 45
        }
      },
      colors: [
        "#2f7ed8",
        "#0d233a",
        "#8bbc21",
        "#1aadce",
        "#492970",
        "#f28f43",
        "#77a1e5",
        "#c42525",
        "#a6c96a"
      ],
      credits: {
        enabled: false
      },
      title: {
        text: "Running Clusters by Template"
      },
      plotOptions: {
        pie: {
          innerSize: 100,
          depth: 45
        },
        series: {
          cursor: "pointer",
          point: {
            events: {
              click: event => {
                this.$router.push({
                  name: "stacks",
                  params: {
                    moduleParam: event.point.name,
                    clusterStatus: "RUNNING"
                  }
                });
              }
            }
          }
        }
      },
      series: [
        {
          name: "Running Clusters",
          data: this.summary.runningStackOptions
        }
      ]
    };
    this.loading = false;
  },
  methods: {
    getRunningStacksByProvider(summary, modules) {
      let counts = [];
      if (summary.stacks) {
        for (let module of modules) {
          let count = summary.stacks.filter(
            item => item.moduleId === module.id && item.state === "RUNNING"
          );
          counts.push([module.name, count.length]);
        }
      }
      return counts;
    },
    getStacksByStatusAndProvider(summary, modules) {
      let categories = this.modules.map(item => item.name);
      let zeroFilled = Array.apply(null, new Array(categories.length)).map(
        Number.prototype.valueOf,
        0
      );
      let counts = [
        {
          name: "New",
          data: zeroFilled
        },
        {
          name: "Running",
          data: zeroFilled
        },
        {
          name: "Stopped",
          data: zeroFilled
        },
        {
          name: "Failed",
          data: zeroFilled
        }
      ];
      if (summary.stacks.length > 0) {
        for (let module of modules) {
          let providerIndex = categories.indexOf(module.name);
          let newStacks = summary.stacks.filter(
            item => item.moduleId === module.id && item.state === "NEW"
          );
          let runnnigStacks = summary.stacks.filter(
            item => item.moduleId === module.id && item.state === "RUNNING"
          );
          let stoppedStacks = summary.stacks.filter(
            item => item.moduleId === module.id && item.state === "STOPPED"
          );
          let failedStacks = summary.stacks.filter(
            item => item.moduleId === module.id && item.state === "FAILED"
          );
          let newStacksData = Object.assign([], counts[0].data);
          newStacksData[providerIndex] = newStacks.length;
          let runningStacksData = Object.assign([], counts[1].data);
          runningStacksData[providerIndex] = runnnigStacks.length;
          let stoppedStacksData = Object.assign([], counts[2].data);
          stoppedStacksData[providerIndex] = stoppedStacks.length;
          let failedStacksData = Object.assign([], counts[3].data);
          failedStacksData[providerIndex] = failedStacks.length;
          counts = [
            {
              name: "New",
              data: newStacksData
            },
            {
              name: "Running",
              data: runningStacksData
            },
            {
              name: "Stopped",
              data: stoppedStacksData
            },
            {
              name: "Failed",
              data: failedStacksData
            }
          ];
        }
      }

      return { categories: categories, series: counts };
    }
  }
};
</script>
