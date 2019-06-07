# ==================
# Top-level metadata
# ==================

%global pybasever 3.7

# pybasever without the dot:
%global pyshortver 37

Name: python3
Summary: Interpreter of the Python programming language
URL: https://www.python.org/

#  WARNING  When rebasing to a new Python version,
#           remember to update the python3-docs package as well
%global general_version %{pybasever}.3
#global prerel ...
%global upstream_version %{general_version}%{?prerel}
Version: %{general_version}%{?prerel:~%{prerel}}
Release: 3%{?dist}
License: Python

Name:           cello
Version:        1.0
Release:        1%{?dist}
Summary:        Hello World example implemented in C

License:        GPLv3+
URL:            https://www.example.com/%{name}
Source0:        https://www.example.com/%{name}/releases/%{name}-%{version}.tar.gz

Patch0:         cello-output-first-patch.patch

BuildRequires:  gcc
BuildRequires:  make

# This  is a comment and should be greyed out
%description
The long-tail description for our Hello World Example implemented in
C.

%prep
%setup -q

%patch0

%build
make %{?_smp_mflags}

%install
%make_install

%files
%license LICENSE
%{_bindir}/%{name}
%{__bzip2<caret>}

%changelog
* Tue May 31 2016 Adam Miller <maxamillion@fedoraproject.org> - 1.0-1
- First cello package

* Tue Oct 27 2009 David Malcolm <dmalcolm@redhat.com> - 3.1.1-6
- reword description, based on suggestion by amcnabb
- fix the test_email and test_imp selftests (patch 3 and patch 4 respectively)
- fix the test_tk and test_ttk_* selftests (patch 5)
- fix up the specfile's handling of shebang/perms to avoid corrupting
test_httpservers.py (sed command suggested by amcnabb)
