# ==================
# Top-level metadata
# ==================

%global pybasever 3.7

# pybasever without the dot:
%global pyshortver 37

%{warn:Doing a main_python build with wrong %%__default_python3_pkgversion (0%{?__default_python3_pkgversion}, but this is %pyshortver)}

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

%if %{with rpmwheels}
Requires: python-setuptools-wheel
Requires: python-pip-wheel
%else
Provides: bundled(python3-pip) = 19.0.3
Provides: bundled(python3-setuptools) = 40.8.0
%endif

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
make %{?_smp_mflags} \
%if %{with rhel8_compat_shims}
  --some-argument \
%else
  --some-other-argument \
%endif
  --some-final-argument

%install
%make_install

%pretrans -p <lua>
path = "/usr/share/hello/world"
st = posix.stat(path)
if st and st.type == "link" then
  os.remove(%{some_path})
end

echo %complete_message

%files
%license LICENSE
%{_bindir}/%{name}

%changelog
* Tue May 31 2016 Adam Miller <maxamillion@fedoraproject.org> - 1.0-1
- First cello package

* Tue Oct 27 2009 David Malcolm <dmalcolm@redhat.com> - 3.1.1-6
- reword description, based on suggestion by amcnabb
- fix the test_email and test_imp selftests (patch 3 and patch 4 respectively)
- fix the test_tk and test_ttk_* selftests (patch 5)
- fix up the specfile's handling of shebang/perms to avoid corrupting
test_httpservers.py (sed command suggested by amcnabb)
