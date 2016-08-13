//
// Created by yazha on 2016-08-10.
//

#include "ProcessBase.h"


bool ProcessBase::create_channel( )
{
}

int ProcessBase::write_to_channel( void* data, int len )
{
    return write( m_channel, data, len );
}

int ProcessBase::read_from_channel( void* data, int len )
{
    return read( m_channel, data, len );
}

int ProcessBase::get_channel() const
{
    return m_channel;
}

void ProcessBase::set_channel( int channel_fd )
{
    m_channel = channel_fd;
}

ProcessBase::ProcessBase()
{

}

ProcessBase::~ProcessBase()
{
    close(m_channel);
}