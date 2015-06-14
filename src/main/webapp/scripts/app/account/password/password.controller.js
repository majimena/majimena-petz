'use strict';

angular.module('majimenatestApp')
    .controller('PasswordController', ['$scope', 'Notify', 'Auth', 'Principal',
    function ($scope, Notify, Auth, Principal) {
        Principal.identity().then(function(account) {
            $scope.account = account;
        });

        $scope.changePassword = function () {
            if ($scope.password !== $scope.confirmPassword) {
                Notify.error('新しいパスワードが確認用のものと異なっています。');
            } else {
                $scope.doNotMatch = null;
                Auth.changePassword($scope.password).then(function () {
                    Notify.success('パスワードを変更しました。');
                }).catch(function () {
                    Notify.error('パスワードの変更ができませんでした。');
                });
            }
        };
    }]);
