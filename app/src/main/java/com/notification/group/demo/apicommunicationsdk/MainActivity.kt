package com.notification.group.demo.apicommunicationsdk

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.notification.group.demo.communicationsdk.CommunicationException
import com.notification.group.demo.communicationsdk.CommunicationManager
import com.notification.group.demo.communicationsdk.ICommunicationResponseProcessor
import com.notification.group.demo.communicationsdk.OnCommunicationResponseListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener, OnCommunicationResponseListener {

    private var TAG :String ="MainActivity"
    private var url :String ="http://dummy.restapiexample.com/api/v1/employees";
//    private var url :String ="https://n-pvt.hungama.com/v2/movieapp/home_v210.php";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_call_api.setOnClickListener(this)
    }

    fun callApi() {
        Log.d(TAG,"inside call")
        CommunicationManager.getInstance()?.performOperation(
            HomeOperation(
                url,
                this))
    }

    override fun onClick(p0: View?) {
        GlobalScope.launch{callApi()}
    }

    override fun onSuccess(operationId: Int, responseProcessor: ICommunicationResponseProcessor?) {
        Log.d("Test", "Success")
        Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()
    }

    override fun onFailure(operationId: Int, exception: CommunicationException?) {
        Log.d("Test", "Failed")
        Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
    }
}
