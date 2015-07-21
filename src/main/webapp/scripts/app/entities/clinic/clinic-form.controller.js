'use strict';

angular.module('petzioApp')
    .controller('ClinicFormController', ['$scope', '$state', '$stateParams', 'Notify', 'Clinic',
    function ($scope, $state, $stateParams, Notify, Clinic) {

        $scope.onLoad = function() {
            var id = $stateParams.id;
            if (id === null || id === 0) {
                return;
            }
            Clinic.get({id: id}, function(result) {
                $scope.clinic = result;
            });
        };

        $scope.save = function () {
            if (angular.isDefined($scope.clinic.id)) {
                Clinic.update($scope.clinic, function () {
                    Notify.success('クリニック情報を更新しました。');
                });
            } else {
                Clinic.save($scope.clinic, function () {
                    Notify.success('新規クリニックを作成しました。');
                });
            }
            $state.go('clinicList');
        };

        $scope.refresh = function () {
            $scope.clinic = {name: null, description: null, owner: null, id: null};
            $scope.form.$setPristine();
            $scope.form.$setUntouched();
        };
    }]);
