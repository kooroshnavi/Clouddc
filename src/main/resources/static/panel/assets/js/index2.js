$(function() {
    "use strict";

	   console.log(eventTypeCount)

       var options = {
        series: eventTypeCount,
        chart: {
        height: 330,
        type: 'pie',
      },

      labels: ['خرابی تجهيز','مشکل در سالن','نصب و راه اندازی','جابجایی','تعویض','بازدید'],
      legend: {
        position: 'bottom'
      },
      responsive: [{
        breakpoint: 480,
        options: {
          chart: {
            height: 300
          },
          legend: {
            position: 'bottom'
          }
        }
      }]
      };

      var chart = new ApexCharts(document.querySelector("#chart0"), options);
      chart.render();









	
	});