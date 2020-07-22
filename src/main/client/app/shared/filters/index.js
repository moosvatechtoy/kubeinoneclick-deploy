import Vue from 'vue';

function formatDate(value, options) {
  if (!value) return '';
  let utcSplitTime = value.split('T');
  let date = utcSplitTime[0] + ' ' + utcSplitTime[1].substring(0, 8) + ' UTC';
  return new Intl.DateTimeFormat(undefined, options).format(new Date(date));
}

function getTimeDuration(value) {
  if (!value) return '';
  let utcSplitTime = value.split('T');
  let endDate = utcSplitTime[0] + ' ' + utcSplitTime[1].substring(0, 8) + ' UTC';
  let startDate = new Date();
  let duration = Math.floor((Date.parse(endDate) - Date.parse(startDate)) / 1000);
  const hours = Math.floor(duration / 3600) % 24;
  const minutes = Math.floor(duration / 60) % 60;
  const seconds = (`0${duration % 60}`).slice(-2);
  return `${hours > 0 ? `${hours} hr` : ''}
                    ${minutes > 0 ? `${minutes} min` : ''}
                    ${seconds} sec`;
}

export default function initFilters() {

  // display date like "05/04/2020, 4:20:20 AM"
  Vue.filter('dateTime', (value) => formatDate(value, {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
    second: '2-digit',
  }));

  // display date like "May 04, 2020, 4:20:20 AM"
  Vue.filter('dateTimeLong', (value) => formatDate(value, {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
    second: '2-digit',
  }));

  Vue.filter('timeDuration', (value) => getTimeDuration(value));

}
