# Installation Minibian

apt-get update
apt-get install nano sudo rpi-update raspi-config usbutils dosfstools -y
apt-get remove initramfs-tools -y

raspi-config
#  Filesystem

rpi-update
reboot
apt-get upgrade -y
apt-get dist-upgrade -y
apt-get install firmware-linux-nonfree wireless-tools wpasupplicant python -y