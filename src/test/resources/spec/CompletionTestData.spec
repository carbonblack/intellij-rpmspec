# ==================
# Top-level metadata
# ==================

%global pybasever 3.7

# pybasever without the dot:
%global pyshortver 37

Name:           cello
Version:        1.0
Release:        1%{?dist}
Summary:        Hello World example implemented in C

License:        GPLv3+
URL:            https://www.example.com/%{pyname}
Source0:        https://www.example.com/%{name}/releases/%{name}-%{py<caret>}.tar.gz

Patch0:         cello-output-first-patch.patch

BuildRequires:  gcc
BuildRequires:  make
