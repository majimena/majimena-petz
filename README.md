# Majimena PETZ 

## About

Majimena PETZ is japanese open source pet clinic management application.

## for Developers
### Environment Variables
#### AWS Credentials

AWSコマンドラインツールがインストールされている場合は、以下のコマンドを実行してAWSの認証情報を登録してください。
```
aws configure
```

認証情報は以下に作成されます。単体テストを実行する際は、プログラムはここで指定したAWSアカウントにアクセスします。
```
cat ~/.aws/credentials
```
