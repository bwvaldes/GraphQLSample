package com.example.graphqlsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.sample.FindQuery
import com.example.graphqlsample.Constants.AUTH_TOKEN
import com.example.graphqlsample.Constants.BASE_URL
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient


/**
 *                              PROJECT LINKS
 *
 * https://medium.com/mindorks/what-is-graphql-and-using-it-on-android-ab8e493abdd7
 * https://www.apollographql.com/docs/android/essentials/get-started.html
 * https://github.com/apollographql/apollo-android
 * https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/
 *
 * */


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findBtn.setOnClickListener(this)
        progressBar.visibility = View.GONE

    }

    override fun onClick(v: View?) {
        when (v) {
            findBtn -> {
                callApi()
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    // OkHTTP Client Adapter & Apollo Builder
    private fun setupApollo(): ApolloClient {
        val okHttp = OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(
                    original.method(),
                    original.body()
                )
                builder.addHeader(
                    "Authorization"
                    , "Bearer $AUTH_TOKEN"
                )
                chain.proceed(builder.build())

            }
            .build()
        return ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttp)
            .build()
    }

    private fun callApi() {
        val client = setupApollo()
        client.query(
            FindQuery
                .builder()
                .name(repoNameEditText.text.toString())
                .owner(ownerNameEditText.text.toString())
                .build()
        )
            // Enqueue runs asynchronously
            .enqueue(object : ApolloCall.Callback<FindQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.i(MainActivity::class.java.simpleName, e.message.toString())
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<FindQuery.Data>) {
                    val logmessage =
                        Log.i(MainActivity::class.java.simpleName, response.data()?.repository().toString())
                    print(logmessage)
                    //User Interface Thread | Updates UI from Database
                    runOnUiThread {

                        progressBar.visibility = View.GONE

                        nameTextView.text =
                                response.data()?.repository()?.name() ?: "N/A"

                        descriptionTextView.text =
                                response.data()?.repository()?.description() ?: "N/A"

//                        forksTextView.text =
//                                response.data()?.repository()?.forkCount().toString()

                        response.data()?.repository()?.forkCount()?.let { forkCount ->
                            forksTextView.text = forkCount.toString()

                        } ?: run {
                            forksTextView.text = "N/A"
                        }


                        urlTextView.text =
                                response.data()?.repository()?.url().toString()
                    }
                }

            })
    }

}
