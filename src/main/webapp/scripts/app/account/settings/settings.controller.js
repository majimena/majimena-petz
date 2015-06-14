'use strict';

angular.module('majimenatestApp')
    .controller('SettingsController', function ($scope, Notify, Principal, Auth) {
        $scope.success = null;
        $scope.error = null;
        Principal.identity(true).then(function(account) {
            $scope.settingsAccount = account;
        });

        $scope.save = function () {
            Auth.updateAccount($scope.settingsAccount).then(function() {
                Notify.success('アカウント設定を保存しました。');
                Principal.identity().then(function(account) {
                    $scope.settingsAccount = account;
                });
            }).catch(function() {
                Notify.success('エラーが発生しました。');
            });
        };
    });
