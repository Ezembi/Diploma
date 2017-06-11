// This file is a part of the IncludeOS unikernel - www.includeos.org
//
// Copyright 2015 Oslo and Akershus University College of Applied Sciences
// and Alfred Bratterud
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include <cmath> // rand()
#include <sstream>
#include <string>

#include <os>
#include <net/inet4>
#include <timers>
#include <net/http/request.hpp>
#include <net/http/response.hpp>

using namespace std::chrono;

uint64_t old_halt_ = 0;
uint64_t new_halt_ = 0;
uint64_t old_total_ = 0;
uint64_t new_total_ = 0;

uint64_t serialized_halt_ = 0;
uint64_t serialized_total_ = 0;

std::string service_handler(const std::string& s) {
    int sp = s.find("/?data=") + 6;
    int fp = s.find(" HTTP");
    
    std::string number = s.substr(sp,fp - sp);
    long myLong = std::stol( number );
    
    int count = 0;
    for (long i = 0; i <= myLong; i++) {
        int divCount = 0;
        for (long j = 1; j <= i; j++) {
            if(i%j == 0){
                divCount++;
            }
        }
        if(divCount == 2){
            count++;
        }
    }
    
    std::stringstream stream;
    stream << "{\"d\":\"simple count = " + std::to_string(count) + "\"}";

    return stream.str();
}

http::Response handle_request(const std::string& s) {
    http::Response res;

    auto& header = res.header();

    header.set_field(http::header::Server, "IncludeOS/0.10");

    res.add_body(service_handler(s));
    header.set_field(http::header::Content_Type, "text/html; charset=UTF-8");
    header.set_field(http::header::Content_Length, std::to_string(res.body().to_string().size()));

    header.set_field(http::header::Connection, "close");

    return res;
}

double getActive() {
    uint64_t temp_halt = new_halt_;
    uint64_t temp_total = new_total_;

    new_halt_ = OS::get_cycles_halt();
    new_total_ = OS::get_cycles_total();
    old_halt_ = temp_halt;
    old_total_ = temp_total;

    if (new_halt_ > old_halt_)
        serialized_halt_ = new_halt_ - old_halt_;

    if (new_total_ > old_total_)
        serialized_total_ = new_total_ - old_total_;

    if (serialized_total_ > serialized_halt_) {
        return serialized_total_ - serialized_halt_;
    } else {
        return 0;
    }
}

void ping() {
    printf("\nGo ping server:");

    int proc = (getActive() * 100.0) / ((OS::cpu_freq().count() * 1000) * 1000);

    if (proc < 100) {
        printf("%d:", proc);
    } else {
        printf("100:");
    }

    printf("%d\n", OS::heap_usage());
}

void Service::start(const std::string& s) {
    printf("Start parametrs: %s\n\n", s.c_str());
    //get ip from start params
    std::size_t ipPos = s.find("!");
    // DHCP on interface 0
    auto& inet = net::Inet4::ifconfig(10.0);
    // static IP in case DHCP fails

    net::Inet4::ifconfig(
            net::ip4::Addr(s.substr(ipPos + 1)), // IP
    {
        255, 255, 255, 0
    }, // Netmask
    {
        10, 0, 0, 1 }, // Gateway
    {
        10, 0, 0, 1 }); // DNS

    // Print some useful netstats every 30 secs
    //TODO: тут будет отправка своeго состояния
    Timers::periodic(5s, 5s,
            [&inet, &s] (uint32_t) {
                printf("<Service(%s)> TCP STATUS:\n%s\n", s.substr(s.find("!") + 1).c_str(), inet.tcp().status().c_str());
                ping();
            });

    // Set up a TCP server on port 80
    auto& server = inet.tcp().bind(80);
    


    // Add a TCP connection handler - here a hardcoded HTTP-service
    server.on_connect(
            [] (auto conn) {
                printf("<Service> @on_connect: Connection %s successfully established.\n",
                        conn->remote().to_string().c_str());
                // read async with a buffer size of 1024 bytes
                // define what to do when data is read
                conn->on_read(1024,
                        [conn] (auto buf, size_t n) {
                            printf("<Service> @on_read: %u bytes received.\n", n);
                            printf("\nbuf: %s\n", buf.get());
                            auto res = handle_request(std::string(reinterpret_cast<char*> (buf.get())));
                            printf("<Service> Responding with %u %s.\n",
                                    res.status_code(), http::code_description(res.status_code()).to_string().c_str());

                            conn->write(res, [](size_t written) {
                                printf("<Service> @on_write: %u bytes written.\n", written);
                            });
                        });
            });

    printf("*** SERVICE STARTED ***\n");
}
