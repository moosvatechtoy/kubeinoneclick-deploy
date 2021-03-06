<template>
  <div
    class="job-step-container"
    :class="step.undefined ? 'disabled' : step.status"
  >
    <!-- step undefined -->
    <div
      v-if="step.undefined"
      class="job-step-header"
    >
      <div class="job-step-header-title">
        <span>{{ headerTitle }}</span>
      </div>
    </div>

    <!-- step defined -->
    <div
      v-if="!step.undefined"
      v-b-toggle="'collapse-' + id"
      class="job-step-header"
    >
      <div class="job-step-header-title">
        <span>{{ headerTitle }}</span>
        <font-awesome-icon
          v-if="step.status === 'STARTED'"
          icon="circle-notch"
          spin
          class="icon"
        />
      </div>
      <font-awesome-icon
        v-if="bodyVisible"
        icon="chevron-up"
        class="icon"
      />
      <font-awesome-icon
        v-else
        icon="chevron-down"
        class="icon"
      />
    </div>
    <b-collapse
      v-if="!step.undefined"
      :id="'collapse-' + id"
      v-model="bodyVisible"
      class="job-step-body"
    >
      <div class="job-step-execution metadata">
        <span class="job-step-status">Status&nbsp;{{ step.status }}</span>
        <p>
          <font-awesome-icon
            icon="play"
            class="icon"
          />
          Started&nbsp;<strong>{{ step.startDateTime }}</strong>
        </p>
        <p v-if="step.endDateTime">
          <font-awesome-icon
            icon="stop"
            class="icon"
          />
          Ended&nbsp;<strong>{{ step.endDateTime }}</strong>
        </p>
        <p>
          <font-awesome-icon
            icon="stopwatch"
            class="icon"
          />
          Duration&nbsp;
          <strong>
            <app-job-timer
              :start-time="step.startDateTime | dateTimeLong"
              :end-time="step.endDateTime | dateTimeLong"
            />
          </strong>
        </p>
      </div>
      <app-console
        :id="'logs-' + id"
        :css-style="'max-height: 300px'"
        :logs="step.logs"
      />
    </b-collapse>
  </div>
</template>

<script>
  import AppJobTimer from '@/pages/stacks/job/job-timer.vue';
  import { AppConsole } from '@/shared/components';

  export default {
    name: 'AppJobStep',
    components: {
      AppJobTimer,
      AppConsole,
    },
    props: {
      id: { type: String, required: true },
      headerTitle: { type: String, required: true },
      step: {
        type: Object,
        default: () => ({ undefined: true }),
      },
    },
    data: () => ({
      bodyVisible: false,
    }),
    watch: {
      step(newValue) {
        this.bodyVisible = newValue.status !== 'FINISHED';
      },
    },
  };
</script>

<style scoped>
  .job-step-container {
    margin: 1rem 0;
  }

  .job-step-container .job-step-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border: 1px solid #eaeaea;
    padding: 1rem 1rem 1rem 2rem;
    font-size: larger;
    font-weight: bolder;
    cursor: pointer;
  }

  .job-step-container .job-step-header .job-step-header-title {
    display: flex;
    align-items: center;
    text-transform: uppercase;
  }

  .job-step-container .job-step-header .job-step-header-title .icon {
    margin-left: 0.5rem;
  }

  .job-step-container .job-step-header .icon {
    font-size: larger;
  }

  .job-step-container .job-step-body {
    border: 1px solid #eaeaea;
    border-top: none;
    padding: 1rem;
  }

  .job-step-container .job-step-body .metadata p {
    font-size: 14px;
  }

  .job-step-container .job-step-body .icon {
    width: 1rem;
    margin-right: 1rem;
    text-align: center;
  }

  .job-step-container .job-step-execution {
    margin-bottom: 1rem;
  }

  .job-step-container .job-step-status {
    font-size: larger;
    margin-bottom: 1rem;
    display: inline-block;
  }

  .job-step-container.disabled .job-step-header {
    background: linear-gradient(to right, #eaeaea 0, #eaeaea 1rem, #fff 1rem, #fff 100%) no-repeat;
    color: #eaeaea;
    cursor: default;
  }

  .job-step-container[class*=STARTED] .job-step-header {
    background: linear-gradient(to right, #2196f3 0, #2196f3 1rem, #fff 1rem, #fff 100%) no-repeat;
    color: #2196f3;
  }

  .job-step-container[class*=FINISHED] .job-step-header {
    background: linear-gradient(to right, #1ed085 0, #1ed085 1rem, #fff 1rem, #fff 100%) no-repeat;
    color: #1ed085;
  }

  .job-step-container[class*=FAILED] .job-step-header {
    background: linear-gradient(to right, #dc3545 0, #dc3545 1rem, #fff 1rem, #fff 100%) no-repeat;
    color: #dc3545;
  }

  .job-step-container[class*=STARTED] .job-step-status {
    color: #2196f3;
  }

  .job-step-container[class*=FINISHED] .job-step-status {
    color: #1ed085;
  }

  .job-step-container[class*=FAILED] .job-step-status {
    color: #dc3545;
  }
</style>
