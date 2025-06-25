# Ten plik definiuje całą naszą warstwę sieciową.

# --- 1. Główny VPC (Virtual Private Cloud) ---
# To nasza prywatna, odizolowana sieć w chmurze AWS.
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.project_name}-vpc"
  }
}


# --- 2. Podsieci Publiczne ---
# Zasoby umieszczone tutaj (np. bramka NAT) będą miały dostęp do internetu.
# Tworzymy dwie podsieci w różnych Strefach Dostępności (Availability Zones) dla wysokiej dostępności.
resource "aws_subnet" "public_a" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "${var.aws_region}a"
  map_public_ip_on_launch = true # Automatycznie przypisuj publiczne IP

  tags = {
    Name = "${var.project_name}-public-subnet-a"
  }
}

resource "aws_subnet" "public_b" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "${var.aws_region}b"
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.project_name}-public-subnet-b"
  }
}


# --- 3. Podsieci Prywatne ---
# Tutaj umieścimy naszą bazę danych i funkcje Lambda.
# Zasoby te nie będą bezpośrednio dostępne z internetu.
resource "aws_subnet" "private_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.101.0/24"
  availability_zone = "${var.aws_region}a"

  tags = {
    Name = "${var.project_name}-private-subnet-a"
  }
}

resource "aws_subnet" "private_b" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.102.0/24"
  availability_zone = "${var.aws_region}b"

  tags = {
    Name = "${var.project_name}-private-subnet-b"
  }
}


# --- 4. Bramka Internetowa i Routing dla Podsieci Publicznych ---
# Pozwala zasobom w podsieciach publicznych komunikować się z internetem.
resource "aws_internet_gateway" "main_gw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-igw"
  }
}

# Tabela routingu dla podsieci publicznych
resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.main.id

  # Reguła: cały ruch na zewnątrz (0.0.0.0/0) kieruj do bramki internetowej.
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main_gw.id
  }

  tags = {
    Name = "${var.project_name}-public-rt"
  }
}

# Powiązanie tabeli routingu z podsieciami publicznymi.
resource "aws_route_table_association" "public_a_assoc" {
  subnet_id      = aws_subnet.public_a.id
  route_table_id = aws_route_table.public_rt.id
}

resource "aws_route_table_association" "public_b_assoc" {
  subnet_id      = aws_subnet.public_b.id
  route_table_id = aws_route_table.public_rt.id
}


# --- 5. NAT Gateway i Routing dla Podsieci Prywatnych ---
# Pozwala zasobom w podsieciach prywatnych na inicjowanie połączeń na zewnątrz (np. w celu pobrania aktualizacji),
# ale blokuje połączenia przychodzące z internetu.
# UWAGA: NAT Gateway jest usługą płatną i będzie zużywać Twoje kredyty w Learner Lab!

resource "aws_eip" "nat_eip" {
  domain = "vpc"
  tags = {
    Name = "${var.project_name}-nat-eip"
  }
}

resource "aws_nat_gateway" "main_nat" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = aws_subnet.public_a.id # Bramka NAT musi znajdować się w podsieci publicznej.

  tags = {
    Name = "${var.project_name}-nat-gw"
  }

  # Zależy od bramki internetowej, aby mieć pewność, że zostanie utworzona w poprawnej kolejności.
  depends_on = [aws_internet_gateway.main_gw]
}

# Tabela routingu dla podsieci prywatnych
resource "aws_route_table" "private_rt" {
  vpc_id = aws_vpc.main.id

  # Reguła: cały ruch na zewnątrz kieruj do bramki NAT.
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main_nat.id
  }

  tags = {
    Name = "${var.project_name}-private-rt"
  }
}

# Powiązanie tabeli routingu z podsieciami prywatnymi.
resource "aws_route_table_association" "private_a_assoc" {
  subnet_id      = aws_subnet.private_a.id
  route_table_id = aws_route_table.private_rt.id
}

resource "aws_route_table_association" "private_b_assoc" {
  subnet_id      = aws_subnet.private_b.id
  route_table_id = aws_route_table.private_rt.id
}


# --- 6. Grupy Bezpieczeństwa (Firewalle) ---
# Domyślnie wszystko jest zablokowane. Tworzymy reguły, aby zezwolić na potrzebny ruch.

# Grupa dla naszych funkcji Lambda
resource "aws_security_group" "lambda_sg" {
  name        = "${var.project_name}-lambda-sg"
  description = "Zezwala na ruch wychodzący dla funkcji Lambda"
  vpc_id      = aws_vpc.main.id

  # Zezwalamy na cały ruch wychodzący.
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-lambda-sg"
  }
}

# Grupa dla naszej bazy danych RDS
resource "aws_security_group" "rds_sg" {
  name        = "${var.project_name}-rds-sg"
  description = "Zezwala na ruch przychodzący do bazy danych z funkcji Lambda"
  vpc_id      = aws_vpc.main.id

  # REGULA PRZYCHODZĄCA: Zezwalamy na ruch na porcie PostgreSQL (5432)
  # tylko i wyłącznie ze źródeł, które należą do grupy bezpieczeństwa naszych Lambd.
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.lambda_sg.id]
  }

  # Zezwalamy na cały ruch wychodzący.
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-rds-sg"
  }
}