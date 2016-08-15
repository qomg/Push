//
// Created by yazha on 2016-08-10.
//

#ifndef PUSH_PARENT_H
#define PUSH_PARENT_H

#include "ProcessBase.h"
#include "Child.h"

/**
 * 功能：父进程的实现
 */
class Parent : public ProcessBase{
public:

    Parent( JNIEnv* env, jobject jobj );

    virtual bool create_child( );

    virtual void do_work();

    virtual void catch_child_dead_signal();

    virtual void on_child_end();

    virtual ~Parent();

    bool create_channel();

/**
* 获取父进程的JNIEnv
*/
    JNIEnv *get_jni_env() const;

/**
* 获取Java层的对象
*/
    jobject get_jobj() const;

private:

    //JNIEnv *m_env;

    jobject m_jobj;

};


#endif //PUSH_PARENT_H
