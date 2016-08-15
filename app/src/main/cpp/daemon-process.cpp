//
// Created by yazha on 2016-08-10.
//
#include <jni.h>
#include <string>
#include "Parent.h"
#include "Utils.h"

extern "C"
{
JNIEXPORT jboolean JNICALL Java_com_boanda_tool_push_WatchDog_createWatcher( JNIEnv*, jobject, jstring, jstring, jstring);

JNIEXPORT jboolean JNICALL Java_com_boanda_tool_push_WatchDog_connectToMonitor( JNIEnv*, jobject );

JNIEXPORT jint JNICALL Java_com_boanda_tool_push_WatchDog_sendMsgToMonitor( JNIEnv*, jobject, jstring );

JNIEXPORT jint JNICALL JNI_OnLoad( JavaVM* , void* );
};

/**
* 全局变量，代表应用程序进程.
*/
ProcessBase *g_process = NULL;

JNIEXPORT jboolean JNICALL Java_com_boanda_tool_push_WatchDog_createWatcher( JNIEnv* env, jobject thiz, jstring user, jstring sock, jstring service )
{
    g_process = new Parent( env, thiz );

    g_process->path = jstringTostr(env, sock);
    g_process->service_name = jstringTostr(env, service);
    g_process->g_userId  = jstringTostr(env, user);

    g_process->catch_child_dead_signal();

    if( !g_process->create_child() )
    {
        LOGE("<<create child error!>>");

        return JNI_FALSE;
    }

    return JNI_TRUE;
}


JNIEXPORT jboolean JNICALL Java_com_boanda_tool_push_WatchDog_connectToMonitor( JNIEnv* env, jobject thiz )
{
    if( g_process != NULL )
    {
        if( g_process->create_channel() )
        {
            return JNI_TRUE;
        }

        return JNI_FALSE;
    }
}