'use strict';

angular.module('petzioApp')
    .controller('ClinicDashboardController', ['$scope', '$stateParams', 'Notify', 'Clinic' ,
    function ($scope, $stateParams, Notify, Clinic) {
        $scope.clinic = {};
        $scope.load = function (id) {
            Clinic.get({id: id}, function(result) {
              $scope.clinic = result;
            });
        };
        $scope.load($stateParams.id);
    }]);
