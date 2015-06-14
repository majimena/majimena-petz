'use strict';

angular.module('majimenatestApp')
    .controller('ProjectFormController', ['$scope', '$state', '$stateParams', 'Notify', 'Project', function ($scope, $state, $stateParams, Notify, Project) {
        $scope.onLoad = function() {
            var id = $stateParams.id;
            if (id === null) {
                return;
            }
            Project.get({id: id}, function(result) {
                $scope.project = result;
            });
        };

        $scope.save = function () {
            if ($scope.project.id !== null) {
                Project.update($scope.project, function () {
                    Notify.success('プロジェクト情報を更新しました。');
                    $state.go('project');
                });
            } else {
                Project.save($scope.project, function () {
                    Notify.success('新規プロジェクトを作成しました。');
                    $state.go('project');
                });
            }
        };

        $scope.refresh = function () {
            $scope.project = {name: null, description: null, owner: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    }]);
