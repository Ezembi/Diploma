# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.5

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/user/Documents/IncludeOS/examples/demo_service

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/user/Documents/IncludeOS/examples/demo_service

# Include any dependencies generated for this target.
include CMakeFiles/service.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/service.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/service.dir/flags.make

CMakeFiles/service.dir/service.cpp.o: CMakeFiles/service.dir/flags.make
CMakeFiles/service.dir/service.cpp.o: service.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/user/Documents/IncludeOS/examples/demo_service/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/service.dir/service.cpp.o"
	/usr/bin/clang++-3.8   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/service.dir/service.cpp.o -c /home/user/Documents/IncludeOS/examples/demo_service/service.cpp

CMakeFiles/service.dir/service.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/service.dir/service.cpp.i"
	/usr/bin/clang++-3.8  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/user/Documents/IncludeOS/examples/demo_service/service.cpp > CMakeFiles/service.dir/service.cpp.i

CMakeFiles/service.dir/service.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/service.dir/service.cpp.s"
	/usr/bin/clang++-3.8  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/user/Documents/IncludeOS/examples/demo_service/service.cpp -o CMakeFiles/service.dir/service.cpp.s

CMakeFiles/service.dir/service.cpp.o.requires:

.PHONY : CMakeFiles/service.dir/service.cpp.o.requires

CMakeFiles/service.dir/service.cpp.o.provides: CMakeFiles/service.dir/service.cpp.o.requires
	$(MAKE) -f CMakeFiles/service.dir/build.make CMakeFiles/service.dir/service.cpp.o.provides.build
.PHONY : CMakeFiles/service.dir/service.cpp.o.provides

CMakeFiles/service.dir/service.cpp.o.provides.build: CMakeFiles/service.dir/service.cpp.o


CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o: CMakeFiles/service.dir/flags.make
CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o: /usr/local/includeos/src/service_name.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/user/Documents/IncludeOS/examples/demo_service/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o"
	/usr/bin/clang++-3.8   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o -c /usr/local/includeos/src/service_name.cpp

CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.i"
	/usr/bin/clang++-3.8  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /usr/local/includeos/src/service_name.cpp > CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.i

CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.s"
	/usr/bin/clang++-3.8  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /usr/local/includeos/src/service_name.cpp -o CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.s

CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o.requires:

.PHONY : CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o.requires

CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o.provides: CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o.requires
	$(MAKE) -f CMakeFiles/service.dir/build.make CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o.provides.build
.PHONY : CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o.provides

CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o.provides.build: CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o


# Object files for target service
service_OBJECTS = \
"CMakeFiles/service.dir/service.cpp.o" \
"CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o"

# External object files for target service
service_EXTERNAL_OBJECTS =

IncludeOS_example: CMakeFiles/service.dir/service.cpp.o
IncludeOS_example: CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o
IncludeOS_example: CMakeFiles/service.dir/build.make
IncludeOS_example: /usr/local/includeos/drivers/libvirtionet.a
IncludeOS_example: /usr/local/includeos/lib/libcrti.a
IncludeOS_example: /usr/local/includeos/lib/libmultiboot.a
IncludeOS_example: /usr/local/includeos/lib/libos.a
IncludeOS_example: /usr/local/includeos/lib/libosdeps.a
IncludeOS_example: /usr/local/includeos/lib/libc++.a
IncludeOS_example: /usr/local/includeos/lib/libc++abi.a
IncludeOS_example: /usr/local/includeos/lib/libos.a
IncludeOS_example: /usr/local/includeos/lib/libc.a
IncludeOS_example: /usr/local/includeos/lib/libm.a
IncludeOS_example: /usr/local/includeos/lib/libg.a
IncludeOS_example: /usr/local/includeos/lib/libgcc.a
IncludeOS_example: /usr/local/includeos/lib/crtend.o
IncludeOS_example: /usr/local/includeos/lib/libcrtn.a
IncludeOS_example: CMakeFiles/service.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/user/Documents/IncludeOS/examples/demo_service/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Linking CXX executable IncludeOS_example"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/service.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/service.dir/build: IncludeOS_example

.PHONY : CMakeFiles/service.dir/build

CMakeFiles/service.dir/requires: CMakeFiles/service.dir/service.cpp.o.requires
CMakeFiles/service.dir/requires: CMakeFiles/service.dir/usr/local/includeos/src/service_name.cpp.o.requires

.PHONY : CMakeFiles/service.dir/requires

CMakeFiles/service.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/service.dir/cmake_clean.cmake
.PHONY : CMakeFiles/service.dir/clean

CMakeFiles/service.dir/depend:
	cd /home/user/Documents/IncludeOS/examples/demo_service && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/user/Documents/IncludeOS/examples/demo_service /home/user/Documents/IncludeOS/examples/demo_service /home/user/Documents/IncludeOS/examples/demo_service /home/user/Documents/IncludeOS/examples/demo_service /home/user/Documents/IncludeOS/examples/demo_service/CMakeFiles/service.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/service.dir/depend

