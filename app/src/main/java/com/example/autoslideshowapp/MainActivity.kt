package com.example.autoslideshowapp

import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE =100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Nextbutton.setOnClickListener(this)
        Backbutton.setOnClickListener(this)
        PlayStopbutton.setOnClickListener(this)

        //Aneroid 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //パーミッションの許可状態を確認する
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                //許可されている
                getContentsInfo()
            }else{
                //許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),PERMISSIONS_REQUEST_CODE)
            }
            //Android 5系以下の場合
        }else {
            getContentsInfo()
        }
    }
    override fun onClick(v: View){
        when(v.id) {
            R.id.Nextbutton -> susumu()
            R.id.Backbutton -> modoru()
            R.id.PlayStopbutton -> saisei_teisi()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getContentsInfo()
                }
        }
    }
    var cursor: Cursor? = null

    private fun getContentsInfo() {
        val resolver = contentResolver
         cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //データの種類
            null, //項目(null = 全項目)
            null, //フィルタ条件(null = フィルタなし)
            null, //フィルタ用パラメータ
            null //ソート (null ソートなし)
        )

        if (cursor!!.moveToFirst()) {
            //indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        }
    }

    private fun susumu() {
        if (mTimer == null) {
            if (cursor!!.moveToNext()) {
                //indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            } else {
                cursor!!.moveToFirst()
                //indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }
        }
    }

    private fun modoru() {
        if (mTimer == null){
        if (cursor!!.moveToPrevious()) {
            //indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        }
        else{
            cursor!!.moveToLast()
            //indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
            }
        }
    }
    override fun onStop() {
        super.onStop()
        cursor!!.close()
    }

    //タイマー
    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0
    private var mHandler = Handler()

        private fun saisei_teisi(){
            if(mTimer == null){
            PlayStopbutton.setImageResource(
                R.drawable.ic_pause_circle_outline_black_24dp
            )

                if (mTimer == null) {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {

                        override fun run() {
                            mTimerSec += 0.1
                            mHandler.post {

                                if (cursor!!.moveToNext()) {

                                    //indexからIDを取得し、そのIDから画像のURIを取得する
                                    val fieldIndex =
                                        cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = cursor!!.getLong(fieldIndex)
                                    val imageUri =
                                        ContentUris.withAppendedId(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            id
                                        )
                                    imageView.setImageURI(imageUri)
                                }
                            }
                        }
                    }, 2000, 2000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定
                }
            }
            else if (mTimer != null){
                PlayStopbutton.setImageResource(
                    R.drawable.ic_play_circle_outline_black_24dp
                )
                mTimer!!.cancel()
                mTimer = null
            }
        }
    }
