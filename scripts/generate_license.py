#!/usr/bin/env python3
"""小脚印激活码生成器（卖家端）

用法:
  python generate_license.py <deviceId8hex> M            # 月费，默认到期 = 当前 + 30 天
  python generate_license.py <deviceId8hex> Y            # 年度，默认到期 = 当前 + 365 天
  python generate_license.py <deviceId8hex> M/Y <unix秒> # 指定到期时间
  python generate_license.py <deviceId8hex> L            # 永久
  python generate_license.py <流水号8hex> G              # 赠送体验码（不绑定设备，任何设备可用）

示例:
  python generate_license.py A1B2C3D4 L
  python generate_license.py A1B2C3D4 M 1999999999

注意: 密钥必须与 App 内 License.SECRET 保持一致；正式发布前请更换随机密钥。
"""

import hashlib
import hmac
import sys
import time

SECRET = b"xiaojiaoyin-license-v1-2026"
ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"


def base32_encode(data: bytes) -> str:
    result = []
    buffer = 0
    bits = 0
    for byte in data:
        buffer = (buffer << 8) | byte
        bits += 8
        while bits >= 5:
            result.append(ALPHABET[(buffer >> (bits - 5)) & 0x1F])
            bits -= 5
    if bits > 0:
        result.append(ALPHABET[(buffer << (5 - bits)) & 0x1F])
    return "".join(result)


def int32_be(value: int) -> bytes:
    return value.to_bytes(4, "big", signed=True)


def generate(device_hex: str, plan: str, expire: int) -> str:
    device = bytes.fromhex(device_hex)
    if len(device) != 4:
        raise ValueError("deviceId 必须是 8 位十六进制（4 字节）")
    if plan not in ("M", "Y", "L", "G"):
        raise ValueError("plan 仅支持 M（月费）、Y（年度）、L（永久）或 G（赠送体验码）")
    payload = b"XY" + device + plan.encode() + int32_be(expire)
    signature = hmac.new(SECRET, payload, hashlib.sha256).digest()[:4]
    encoded = base32_encode(payload + signature)
    return "-".join(encoded[i : i + 6] for i in range(0, len(encoded), 6))


def main() -> None:
    if len(sys.argv) < 3:
        print(__doc__)
        sys.exit(1)
    device_hex = sys.argv[1].upper()
    plan = sys.argv[2].upper()
    if plan in ("L", "G"):
        expire = 0
    elif len(sys.argv) >= 4:
        expire = int(sys.argv[3])
    elif plan == "Y":
        expire = int(time.time()) + 365 * 24 * 3600
    else:
        expire = int(time.time()) + 30 * 24 * 3600
    code = generate(device_hex, plan, expire)
    print(f"设备 ID: {device_hex}")
    plan_name = {"M": "月费", "Y": "年度", "L": "永久", "G": "赠送体验码（不绑定设备）"}[plan]
    print(f"套餐: {plan_name}")
    if plan not in ("L", "G"):
        print(f"到期: {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(expire))}")
    print(f"激活码: {code}")


if __name__ == "__main__":
    main()
