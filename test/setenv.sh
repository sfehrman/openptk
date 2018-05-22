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

MVN="mvn"

PROFILES_SRVC="srvc-derby,srvc-jndi,srvc-mysql,srvc-oim10g,srvc-oim11g,srvc-oracledb,srvc-spml,srvc-spml2,srvc-unboundid"
PROFILES_APP="app-cli,app-identitycentral,app-portlets,app-register,app-soapws,app-usermgmtlite"
PROFILES_SAMPLE="sample-auth,sample-client,sample-framework,sample-taglib"
PROFILES_ALL="server,${PROFILES_SRVC},${PROFILES_APP},${PROFILES_SAMPLE}"
