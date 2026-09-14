# TranAntiCheat

PvP sunuculari icin FLY-only anti-cheat plugin'i. Paper 1.21.x uyumlu.

## Ozellikler

- **Fly tespiti**
  - Yukari yukselis (Ascend) kontrolu
  - Havada askida kalma (Hover) kontrolu
  - Havada uzun sure kalma (AirTime) kontrolu — varsayilan 120 tick (6 sn)
- **Combat tespiti**
  - KillAura: cok hizli hedef degistirme (MultiAura), hedefe bakmadan vurma (Angle), asiri uzak vurus (Reach)
  - Aim (AimAssist): tick basina asiri yaw donusu (instant-turn / hizli nişan alma)
  - TriggerBot: cok kisa aralikli surekli saldiri serileri (FastAttack) — varsayilan kapali
- **Ceza sistemi (kademeli)**
  1. vl 1-2: Yetkililere alert bildirimi
  2. vl 3: Kick (uyari kicki)
  3. vl 4+: Kalici ban
- **Giris taramasi (join-scan)**
  - Bilinen hileli istemci markalari (brand) engellenir — killaura/triggerbot/aimassist iceren client'lar dahil
  - Gecmis Flag loglarina gore otomatik islem
- **Detayli loglama** (`plugins/TranAntiCheat/logs/`)
- Muafiyet: Essentials/fly, elytra, yaratici/gezici mod, su, merdiven, levitasyon ve arac icin

## Kurulum

1. `TranAntiCheat-1.0.0.jar` dosyasini `plugins/` klasorune kopyala
2. Sunucuyu yeniden baslat
3. `trananticheat reload` ile config'i yeniden yukleyebilirsin

## Komutlar

| Komut | Aciklama |
|---|---|
| `/trananticheat reload` | Config'i yeniden yukler |
| `/trananticheat alerts` | Alert bildirimlerini ac/kapat |
| `/trananticheat help` | Yardim listesi |

Aliases: `/tac`, `/tranac`

## Ayarlar (config.yml)

Anahtar alanlar:

- `punishments.kick-after` — kac ihlalden sonra kick (varsayilan 3)
- `punishments.ban-after` — kac ihlalden sonra ban (varsayilan 4)
- `punishments.ban-duration-seconds` — ban suresi (-1 = kalici)
- `checks.fly.max-air-ticks` — havada kalinabilecek maks tick (varsayilan 120)
- `checks.fly.decay-seconds` — ihlal azaltma suresi (varsayilan 90 sn)
- `checks.killaura.*` — MultiAura suresi, max-bakis acisi, max-reach
- `checks.aim.*` — tick basina max yaw donusu ve tekrar tick sayisi
- `checks.triggerbot.*` — max saldiri araligi ve seri sayisi
- `join-scan.blocked-clients` — giri\u015fte engellenecek istemci markalari

## Gelistirme

Kaynak kodu `src/` altindadir. Proje Maven uyumludur:

```
mvn package
```

- Yapimci: **wenxas**
- Uyumluluk: Paper 1.21.x / Java 21