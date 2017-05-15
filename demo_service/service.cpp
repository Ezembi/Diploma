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

std::string HTML_RESPONSE() {
    /* HTML */
    std::stringstream stream;
    stream << "{\"d\":\"Servise cant parse you request :(\"}";

    return stream.str();
}

std::string HTML_RESPONSE(const std::string& s) {

    int a_count = 0;
    for (int i = 0; i < s.length(); i++) {
        if (s[i] == 'a') {
            a_count++;
        }
    }

    /* HTML */
    std::stringstream stream;
    stream << "{\"d\":\"a = " + std::to_string(a_count) + "\"}";

    return stream.str();
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

http::Response handle_request(const http::Request& req) {
    printf("<Service> Request:\n%s\n", req.to_string().c_str());

    http::Response res;

    auto& header = res.header();

    header.set_field(http::header::Server, "IncludeOS/0.10");

    res.add_body(HTML_RESPONSE(req.uri().str()));
    header.set_field(http::header::Content_Type, "text/html; charset=UTF-8");
    header.set_field(http::header::Content_Length, std::to_string(res.body().to_string().size()));

    header.set_field(http::header::Connection, "close");

    return res;
}

http::Response handle_request() {
    http::Response res;

    auto& header = res.header();

    header.set_field(http::header::Server, "IncludeOS/0.10");

    // GET /
    //res.set_status_code(http::Bad_Request);
    res.add_body(HTML_RESPONSE());
    // set Content type and length
    header.set_field(http::header::Content_Type, "text/html; charset=UTF-8");
    header.set_field(http::header::Content_Length, std::to_string(res.body().to_string().size()));
    res.set_status_code(http::Bad_Request);

    header.set_field(http::header::Connection, "close");

    return res;
}

void Service::stop() {
    printf("\n\nService::stop\n\n");
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
                            try {
                                // try to parse the request
                                http::Request req{(const char*) buf.get(), n};

                                // handle the request, getting a matching response
                                auto res = handle_request(req);

                                printf("<Service> Responding with %u %s.\n",
                                res.status_code(), http::code_description(res.status_code()).to_string().c_str());

                                conn->write(res, [](size_t written) {
                                    printf("<Service> @on_write: %u bytes written.\n", written);
                                });
                            } catch (const std::exception& e) {
                                printf("<Service> Unable to parse request.\n %s\n\n > ", e.what());
                                auto res = handle_request();
                                printf("<Service> Responding with %u %s.\n",
                                res.status_code(), http::code_description(res.status_code()).to_string().c_str());

                                conn->write(res, [](size_t written) {
                                    printf("<Service> @on_write: %u bytes written.\n", written);
                                });
                            }
                        });
            });

    printf("*** SERVICE STARTED ***\n");
}
