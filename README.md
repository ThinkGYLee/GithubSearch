# Github Search
채용 과제로 많이 나오는 항목들을 구현하기 위한 테스트 앱입니다.

## 주요기능
- 깃허브 Api를 이용한 유저 검색
- 즐겨찾기
- 깃허브 OAuth를 이용한 인증, 로그인
- 네트워크 콜 최적화
- 페이징

## 기술 스택
 구분 | 내용
-- | --
Architecture | MVVM, 안드로이드 권장 아키텍쳐
Jetpack | Navigation, Compose, Lifecycle, ViewModel, Room, Paging3
Network | Retrofit, OkHttp, Gson
Asynchronous Processing | Coroutine, Flow
Dependency Injection | Hilt
Third Party Library | Glide

## 스크린샷
| 목록 | 상세 | 검색 |
| --- | --- | --- |
| <img src="https://github.com/user-attachments/assets/4c1061cd-1ad6-4e81-b1a2-b1ee4a620191" width="200"/> | <img src="https://github.com/user-attachments/assets/608b71c3-7129-48fe-97d7-f6e18df4509b" width="200"/> | <img src="https://github.com/user-attachments/assets/ccd59b9f-fea9-429e-8a3c-cc8af3b2bb68" width="200"/> |

| 즐겨찾기 | 인증 | 설정 |
| --- | --- | --- |
| <img src="https://github.com/user-attachments/assets/ca90974a-b0e2-45eb-9737-80229fcca313" width="200"/> | <img src="https://github.com/user-attachments/assets/92cbf7d1-c70f-42a4-b5a1-36291caa2538" width="200"/> | <img src="https://github.com/user-attachments/assets/9bc4015e-0ce5-4e06-ab15-5a8f8a3184cc" width="200"/> |

## Architecture
![img](https://lh6.googleusercontent.com/jIm6sL0mqukk0OROYyStYNsBulEFLZki-z2Y9OD73K-cpvEre-VP1wmdSC-bDpNJrGdhB4bOZbABRspBcn4FJCtJs4uQKKwWesOdThS-B75HwnCdTCqEKXAClxOimOtIu9WbabaP_Mpel6dDpLSSQVk)

### Module
본 프로젝트는 Single-module 구조입니다.

## Github OAuth 로그인 구현

### Github OAuth App 생성

- 아래의 링크에서 New OAuth App을 눌러서 만든다.

https://github.com/settings/developers

![Image](https://github.com/user-attachments/assets/78a6e533-d112-47a0-a657-3f0e9e6db0b0)

![Image](https://github.com/user-attachments/assets/8f9520a7-6227-49d3-997a-a59b20b29b80)

- Application name : 프로젝트의 이름
- Homepage URL : 개인 홈페이지 주소
- Authorization callback URL :
    - 인증 후에 돌아갈 URL
    - AndroidManifest 인텐트 필터에 추가

- 앱의 Client ID 와 Client secrets를 취득할 수 있다.
    - 인증에 필요하니 따로 보관해둔다.
    - local properties에 등록한다.

### Manifest 설정

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

//인터넷 permission 설정

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        ...
        <activity
           ...
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            
//인증 후에 앱으로 돌아올 callback 설정            

            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data
                    android:host="github-auth"
                    android:scheme="git-user-search" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### Local Properties 세팅

BuildConfig를 통해서 호출한다

```kotlin
## This file must *NOT* be checked into Version Control Systems,
# as it contains information specific to your local configuration.
#
# Location of the SDK. This is only used by Gradle.
# For customization when using a Version Control System, please read the
# header note.
#Mon May 27 02:13:48 KST 2024
sdk.dir=/Users/dev/Library/Android/sdk

CLIENT_ID="clien id를 넣는다"
CLIENT_SECRET="client secret을 넣는다"
```

### 로그인 구현

- MainActivity.kt

```kotlin
//MainActivity
//로그인 구현
	fun login(context: Context) {
        val clientId = BuildConfig.CLIENT_ID
        val loginUrl = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", clientId)
            .build()

        val customTabsIntent = CustomTabsIntent.Builder().build()

        customTabsIntent.intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(context, loginUrl)
    }
//로그인 후에 액세스 토근 받아오기
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.getQueryParameter("code")?.let {
            if (it.isNotEmpty()) {
                viewModel.getAccessToken(it)
            } else {
                println("Error exists check your network status")
            }
        }
    }
```

### AccessToken 가져오기

- AccessService.kt

```kotlin
interface AccessService {
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Response<GithubAccessResponse>
}
```

- NetworkModule.kt

```kotlin
    @Singleton
    @Provides
    @TypeAccess
    fun provideAccessOkHttpClient(): OkHttpClient = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }
		
		@Singleton  
		@Provides
    @TypeAccess
    fun provideAccessRetrofit(@TypeAccess okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(accessUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @TypeAccess
    fun provideAccessGithubApi(@TypeAccess retrofit: Retrofit): AccessService {
        return retrofit.create(AccessService::class.java)
    }
```

- Qualifier.kt

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TypeAccess
```

- Repository

```kotlin
override suspend fun getAccessToken(
        id: String,
        secret: String,
        code: String
    ) = accessService.getAccessToken(clientId = id, clientSecret = secret, code = code)
```

- MainViewModel

```kotlin
private val _alertLoginSuccess = MutableSharedFlow<Boolean>()
val alertLoginSuccess: SharedFlow<Boolean> = _alertLoginSuccess

fun getAccessToken(code: String) {
        viewModelScope.launch {
        
        //AccessToken 가져오기
            val response = repository.getAccessToken(
                id = BuildConfig.CLIENT_ID,
                secret = BuildConfig.CLIENT_SECRET,
                code = code
            )
						
						
				//성공적했을 때 로그인 처리
            if (response.isSuccessful && response.code() == 200) {
                response.body().let {
                    if (it != null) {
                        preferenceUtil.setString(str = it.accessToken)
                        _alertLoginSuccess.emit(true)
                    }
                }
            } else {
            
         //실패했을 때 로그인 실패 처리
                _alertLoginSuccess.emit(false)
            }
        }
    }
```

