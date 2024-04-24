$(function() {
    "use strict";
	
    
	// chart 1
	
	var options1 = {
    chart: {
      type: 'line',
      height: 340,
      sparkline: {
        enabled: false
      }
    },
    dataLabels: {
        enabled: false
    },
    fill: {
      type: 'gradient',
        gradient: {
            shade: 'light',
            gradientToColors: [ '#8f50ff'],
            shadeIntensity: 1,
            type: 'vertical',
            opacityFrom: 1,
            opacityTo: 1,
            stops: [0, 100, 100, 100]
        },
    },
    colors: ["#8932ff"],
    series: [{
      name: 'گردش کار',
      data: [0, 0, 0, 0, 0, 0, 0, 2, 11, 20, 0, 0]
    }],
    xaxis: {
      categories: ['فروردین', 'اردیبهشت', 'خرداد', 'تیر', 'مرداد', 'شهریور', 'مهر', 'آبان', 'آذر', 'دی', 'بهمن', 'اسفند'],
    },
    plotOptions: {
			bar: {
		columnWidth: '20%',
		  //endingShape: 'rounded',
				dataLabels: {
					position: 'top', // top, center, bottom
				},
			}
		},
		grid:{
                show: true,
                borderColor: 'rgba(66, 59, 116, 0.15)',
            },
    stroke: {
            width: 3.5, 
            curve: 'smooth',
            dashArray: [0]
       },
    tooltip: {
              theme: 'dark',
              x: {
                  show: false
              },

          }
  }

  new ApexCharts(document.querySelector("#chart1"), options1).render();

	
	
	// chart 2
	
	var options1 = {
      chart: {
        type: 'area',
        height: 50,
        sparkline: {
          enabled: true
        }
      },
      dataLabels: {
          enabled: false
      },
      fill: {
        type: 'gradient',
          gradient: {
              shade: 'light',
              //gradientToColors: [ '#8f50ff'],
              shadeIntensity: 1,
              type: 'vertical',
              opacityFrom: 0.8,
              opacityTo: 0.4,
              stops: [0, 100, 100, 100]
          },
      },
      colors: ["#d80898"],
      series: [{
        name: 'دما',
        data: [25, 66, 41, 59, 25, 44, 12, 36, 9, 21]
      }],
      stroke: {
              width: 2.5, 
              curve: 'smooth',
              dashArray: [0]
         },
      tooltip: {
                theme: 'dark',
                x: {
                    show: false
                },

            }
    }

    new ApexCharts(document.querySelector("#chart2"), options1).render();
	
	
	
	
	// chart 3
	
	var options1 = {
      chart: {
        type: 'area',
        height: 50,
        sparkline: {
          enabled: true
        }
      },
      dataLabels: {
          enabled: false
      },
      fill: {
        type: 'gradient',
          gradient: {
              shade: 'light',
              //gradientToColors: [ '#8f50ff'],
              shadeIntensity: 1,
              type: 'vertical',
              opacityFrom: 0.8,
              opacityTo: 0.4,
              stops: [0, 100, 100, 100]
          },
      },
      colors: ["#02cc89"],
      series: [{
        name: 'دما',
        data: [12, 14, 2, 47, 32, 44, 14, 55, 41, 69]
      }],
      stroke: {
              width: 2.5, 
              curve: 'smooth',
              dashArray: [0]
         },
      tooltip: {
                theme: 'dark',
                x: {
                    show: false
                },

            }
    }

    new ApexCharts(document.querySelector("#chart3"), options1).render();
	
	
	
	
	// chart 4
	
	var options1 = {
      chart: {
        type: 'area',
        height: 50,
        sparkline: {
          enabled: true
        }
      },
      dataLabels: {
          enabled: false
      },
      fill: {
        type: 'gradient',
          gradient: {
              shade: 'light',
              //gradientToColors: [ '#8f50ff'],
              shadeIntensity: 1,
              type: 'vertical',
              opacityFrom: 0.8,
              opacityTo: 0.4,
              stops: [0, 100, 100, 100]
          },
      },
      colors: ["#1b56d6"],
      series: [{
        name: 'دما',
        data: [47, 45, 74, 32, 56, 31, 44, 33, 45, 19]
      }],
      stroke: {
              width: 2.5, 
              curve: 'smooth',
              dashArray: [0]
         },
      tooltip: {
                theme: 'dark',
                x: {
                    show: false
                },

            }
    }

    new ApexCharts(document.querySelector("#chart4"), options1).render();
	
	
	
	// chart 5
	
	var options1 = {
      chart: {
        type: 'area',
        height: 50,
        sparkline: {
          enabled: true
        }
      },
      dataLabels: {
          enabled: false
      },
      fill: {
        type: 'gradient',
          gradient: {
              shade: 'light',
              //gradientToColors: [ '#8f50ff'],
              shadeIntensity: 1,
              type: 'vertical',
              opacityFrom: 0.8,
              opacityTo: 0.4,
              stops: [0, 100, 100, 100]
          },
      },
      colors: ["#f1b307"],
      series: [{
        name: 'دما',
        data: [15, 75, 47, 65, 14, 32, 19, 54, 44, 61]
      }],
      stroke: {
              width: 2.5, 
              curve: 'smooth',
              dashArray: [0]
         },
      tooltip: {
                theme: 'dark',
                x: {
                    show: false
                },

            }
    }

    new ApexCharts(document.querySelector("#chart5"), options1).render();
	
	
	
	// chart 6
	
	var options = {
      chart: {
        height: 290,
        type: 'radialBar',
        toolbar: {
          show: false
        }
      },
      plotOptions: {
        radialBar: {
          //startAngle: -135,
          //endAngle: 225,
           hollow: {
            margin: 0,
            size: '85%',
            background: 'transparent',
            image: undefined,
            imageOffsetX: 0,
            imageOffsetY: 0,
            position: 'front',
            dropShadow: {
              enabled: true,
              top: 3,
              left: 0,
              blur: 4,
              //color: 'rgba(209, 58, 223, 0.65)',
              opacity: 0.12
            }
          },
          track: {
            background: '#fff',
            strokeWidth: '30%',
            margin: 0, // margin is in pixels
            dropShadow: {
              enabled: true,
              top: -3,
              left: 0,
              blur: 4,
			  color: 'rgba(209, 58, 223, 0.65)',
              opacity: 0.12
            }
          },
          dataLabels: { 
            showOn: 'always',
            name: {
              offsetY: -20,
              show: true,
              color: '#3e3d3d',
              fontSize: '15px'
            },
            value: {
              formatter: function (val) {
						return val + "%";
					},
              color: '#3e3d3d',
              fontSize: '40px',
              show: true,
			  offsetY: 10,
            }
          }
        }
      },
      colors: ["#09aaef"],
      series: [88],
      stroke: {
        //lineCap: 'round',
        dashArray: 4
      },
      labels: [''],

    }

    var chart = new ApexCharts(
      document.querySelector("#chart6"),
      options
    );

    chart.render();	 
	
	
	
	// chart 7

     var options = {
            chart: {
                height: 300,
                type: 'bar',
                zoom: {
                      enabled: false
                    },
             foreColor: '#4e4e4e',
             toolbar: {
                  show: false
                },
          sparkline:{
              enabled:false,
            },
            dropShadow: {
                    enabled: false,
                    opacity: 0.15,
                    blur: 3,
                    left: -7,
                    top: 15,
                    //color: 'rgba(0, 158, 253, 0.65)',
                }
            },
            plotOptions: {
                bar: {
            columnWidth: '50%',
              endingShape: 'rounded',
                    dataLabels: {
                        position: 'top', // top, center, bottom
                    },
                }
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                width: 2, 
                curve: 'smooth'
            },
            series: [{
                name: '`C',
                data: [25, 22, 24, 28, 30, 29, 27]
            }],

            xaxis: {
                type: 'day',
                categories: [ "جمعه", "پنج شنبه", "چهارشنبه", "سه شنبه", "دوشنبه", "یکشنبه", "شنبه"]
            },
            yaxis: {
                axisBorder: {
                    show: false
                },
                axisTicks: {
                    show: false,
                },
                labels: {
                    show: false,
                    formatter: function(val) {
                    return parseInt(val);
                  }
                }

            },
			fill: {
                type: 'gradient',
                gradient: {
                    shade: 'light',
                    //gradientToColors: ['#ee0979'],
                    shadeIntensity: 1,
                    type: 'vertical',
                    opacityFrom: 0.7,
                    opacityTo: 0.2,
                    stops: [0, 100, 100, 100]
                },
            },
            colors: ['#ff3838'],
            grid:{
                show: true,
                borderColor: 'rgba(66, 59, 116, 0.15)',
            },
            tooltip: {
                theme: 'dark',
                x: {
                    show: false
                },

            }
        }

        var chart = new ApexCharts(
            document.querySelector("#chart7"),
            options
        );

        chart.render();
	
	
	
	     //chart 8
             
       var options = {
        series: [200, 280, 100, 300, 50],
        chart: {
        height: 330,
        type: 'pie',
      },
      labels: ['سروش', 'ایتا', 'گپ','سبزسیستم','امین آسیا'],
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

      var chart = new ApexCharts(document.querySelector("#chart8"), options);
      chart.render();
	
	
	
	
	
	
	
	
	
	
	});