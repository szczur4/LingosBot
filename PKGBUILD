# Maintainer: szczur4 <rybaglut1@gmail.com>
pkgname=szczur4-lingosbot
pkgver=v2.0r
pkgrel=1
pkgdesc='A bot for lingos.pl'
arch=(any)
url='https://github.com/szczur4/LingosBot'
license=('unknown')
depends=('java-runtime>=25')
makedepends=()
source=("$pkgname-$pkgver.jar::https://github.com/szczur4/LingosBot/releases/download/$pkgver/LingosBot.$pkgver.jar")
sha256sums=('20b71e1f95340f7c0b329ae9c95f1b089516d71f41053b80e40407ae52833a26')
package(){
  install -d "$pkgdir/usr/share/java/$pkgname"
  install -dm777 "$pkgdir/var/lib/$pkgname"
  install -Dm644 "$srcdir/$pkgname-$pkgver.jar" "$pkgdir/usr/share/java/$pkgname/$pkgname.jar"
  install -d "$pkgdir/usr/bin"
  cat>"$pkgdir/usr/bin/$pkgname"<<EOF
#!/bin/sh
cd /var/lib/$pkgname
exec java -jar /usr/share/java/$pkgname/$pkgname.jar "\$@"
EOF
  install -d $pkgdir/usr/lib/systemd/system
  cat>$pkgdir/usr/lib/systemd/system/$pkgname.service<<EOF
[Unit]
Wants=network.target
After=network.target
Description=szczur4 Lingos bot
[Service]
Type=oneshot
ExecStart=/usr/bin/szczur4-lingosbot autostart
EOF
  chmod 755 "$pkgdir/usr/bin/$pkgname"
}