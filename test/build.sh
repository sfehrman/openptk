#! /bin/sh
#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
# 
#      Portions Copyright 2012 Project OpenPTK
# 
# The contents of this file are subject to the terms of the
# Common Development and Distribution License, Version 1.0 only
# (the "License").  You may not use this file except in compliance
# with the License.
# 
# You can obtain a copy of the license at
# trunk/openptk/resource/legal-notices/OpenPTK.LICENSE
# or https://openptk.dev.java.net/OpenPTK.LICENSE.
# See the License for the specific language governing permissions
# and limitations under the License.
# 
# When distributing Covered Code, include this CDDL HEADER in each
# file and include the reference to
# trunk/openptk/resource/legal-notices/OpenPTK.LICENSE. If applicable,
# add the following below this CDDL HEADER, with the fields enclosed
# by brackets "[]" replaced with your own identifying information:
#      Portions Copyright [yyyy] [name of copyright owner]
# 

CWD="`pwd`"
ARG="install"

if [ "${OPENPTK_HOME}" == "" ]; then
   echo "Environment variable OPENPTK_HOME is not set."
   exit 1
fi

. ${OPENPTK_HOME}/test/setenv.sh

COMMAND="${MVN} ${ARG} -P ${PROFILES_ALL}"

echo "JAVA_HOME=${JAVA_HOME}"
echo "OPENPTK_HOME=${OPENPTK_HOME}"

cd ${OPENPTK_HOME}
eval "${COMMAND}"
cd ${CWD}

exit 0
